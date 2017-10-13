package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import javassist.compiler.NoFieldException;
import net.netty.exceptions.MessageDecodeException;
import net.netty.exceptions.UnsupportMessageFieldException;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.inbound.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by CarroNailo on 2017/9/6 18:29 for TestNewServerFramework.
 */
public class InternalClientDecoder extends ByteToMessageDecoder
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		if (in.readableBytes() > 2)
		{
			int pos = in.readerIndex();
			int len = in.readShort();
			if (in.readableBytes() >= len)
			{
				byte cid = in.readByte();
				byte mid = in.readByte();
				ByteBuf content = in.readBytes(len - 2);
				Object msg = DecodeByID(ctx, cid, mid, content);
				content.release();
				if (msg != null)
					out.add(msg);
				else
					out.add(new Object());
			}
			else
				in.readerIndex(pos);
		}
	}

	private Object DecodeByID(ChannelHandlerContext ctx, int cid, int mid, ByteBuf content)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
//			String username = ((ExtendedNioSocketChannel) channel).username;
//			System.out.println(String.format("[%s]收到消息[%d][%d]", username, cid, mid));

			Class msgClazz = InBoundMessageMap.getInstance().getMessageClassByID(cid, mid);
			if(msgClazz != null)
				return DecodeMsg(msgClazz, content);
			else
			{
				if(cid == 37)
				{
					if (mid >= 2 && mid <= 5)
						return new SecurityMsg();
					else if (mid >= 20 && mid <= 25)
						return new SHA1Msg();
				}
			}
		}
		return null;
	}

	private Object DecodeMsg(Class<?> clazz, ByteBuf content)
	{
		Object msg = null;
		try
		{
			msg = DecodeObject(clazz, content);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msg;
	}

	private Object DecodeObject(Class<?> clazz, ByteBuf content) throws Exception
	{
		Object obj = clazz.newInstance();
		Field[] fs = clazz.getFields();        // 获取所有 PUBLIC 变量，包含自己定义的和继承的
		for (Field f : fs)
			DecodeField(obj, f, content);
		return obj;
	}

	private Object DecodePrimitive(Class<?> clazz, ByteBuf content) throws Exception
	{
		if (clazz == boolean.class || clazz == Boolean.class)
			return content.readBoolean();
		else if (clazz == byte.class || clazz == Byte.class)
			return content.readByte();
		else if (clazz == char.class || clazz == Character.class)
			return content.readChar();
		else if (clazz == short.class || clazz == Short.class)
			return content.readShort();
		else if (clazz == int.class || clazz == Integer.class)
			return content.readInt();
		else if (clazz == long.class || clazz == Long.class)
			return content.readLong();
		else if (clazz == float.class || clazz == Float.class)
			return content.readFloat();
		else if (clazz == double.class || clazz == Double.class)
			return content.readDouble();
		else if(clazz == String.class)
		{
			short len = content.readShort();
			byte[] bytes = new byte[len];
			content.readBytes(bytes);
			return new String(bytes, "UTF8");
		}
		else if (clazz == void.class)
			return null;
		else
			return null;
	}

	private void DecodeField(Object obj, Field field, ByteBuf content) throws Exception
	{
		try
		{
			Class<?> fieldClazz = field.getType();
			if (fieldClazz.isPrimitive() || isPrimitiveWrapClass(fieldClazz) || fieldClazz == String.class)
				DecodePrimitiveField(obj, field, content);
			else if (fieldClazz.isArray())
				DecodeArrayField(obj, field, content);
			else if (fieldClazz.isEnum())
				throw new UnsupportMessageFieldException(obj, field.getName());
			else
				DecodeCustomField(obj, field, content);
		}
		catch (MessageDecodeException | UnsupportMessageFieldException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new MessageDecodeException(obj, field.getName(), ex);
		}
	}

	private void DecodePrimitiveField(Object obj, Field field, ByteBuf content) throws Exception
	{
		Class<?> fieldClazz = field.getType();
		if (fieldClazz == boolean.class || fieldClazz == Boolean.class)
			field.setBoolean(obj, content.readBoolean());
		else if (fieldClazz == byte.class || fieldClazz == Byte.class)
			field.setByte(obj, content.readByte());
		else if (fieldClazz == char.class || fieldClazz == Character.class)
			field.setChar(obj, content.readChar());
		else if (fieldClazz == short.class || fieldClazz == Short.class)
			field.setShort(obj, content.readShort());
		else if (fieldClazz == int.class || fieldClazz == Integer.class)
			field.setInt(obj, content.readInt());
		else if (fieldClazz == long.class || fieldClazz == Long.class)
			field.setLong(obj, content.readLong());
		else if (fieldClazz == float.class || fieldClazz == Float.class)
			field.setFloat(obj, content.readFloat());
		else if (fieldClazz == double.class || fieldClazz == Double.class)
			field.setDouble(obj, content.readDouble());
		else if (fieldClazz == void.class)
			throw new UnsupportMessageFieldException(obj, field.getName());
		else if (fieldClazz == String.class)
		{
			short len = content.readShort();
			byte[] bytes = new byte[len];
			content.readBytes(bytes);
			field.set(obj, new String(bytes, "UTF8"));
		}
		else
			throw new UnsupportMessageFieldException(obj, field.getName());
	}

	private void DecodeArrayField(Object obj, Field field, ByteBuf content) throws Exception
	{
		Class<?> elemClazz = field.getType().getComponentType();
		short len = content.readShort();
		Object newArray = Array.newInstance(elemClazz, len);
		if(elemClazz.isPrimitive() || isPrimitiveWrapClass(elemClazz) || elemClazz == String.class)
		{
			for (int i = 0; i < len; ++i)
			{
				Object elem = DecodePrimitive(elemClazz, content);
				if (elem == null)
					throw new MessageDecodeException(obj, field.getName());
				Array.set(newArray, i, elem);
			}
		}
		else
		{
			for (int i = 0; i < len; ++i)
			{
				Object elem = DecodeObject(elemClazz, content);
				if (elem == null)
					throw new MessageDecodeException(obj, field.getName());
				Array.set(newArray, i, elem);
			}
		}
		field.set(obj, newArray);
	}

	private void DecodeCustomField(Object obj, Field field, ByteBuf content) throws Exception
	{
		Object fieldObj = DecodeObject(field.getType(), content);
		if (fieldObj == null)
			throw new MessageDecodeException(obj, field.getName());
		field.set(obj, fieldObj);
	}

	private boolean isPrimitiveWrapClass(Class<?> clazz) throws Exception
	{
		Field f = null;
		try
		{
			f = clazz.getField("DataType");

		}
		catch(NoSuchFieldException ignored)
		{
		}
		if(f != null)
		{
			Class<?> c = (Class)f.get(null);
			if(c != null)
			{
				return c.isPrimitive();
			}
			return false;
		}
		return false;
	}
}
