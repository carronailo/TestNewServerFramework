package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.netty.exceptions.MessageDeserializeException;
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
//			switch (cid)
//			{
//				case 0:    // 登录
//					switch (mid)
//					{
//						case 0:        // 登录返回
//							return DecodeMsg(LoginReturnMsg.class, content);
//						case 1:
//							return DecodeMsg(NoRoleMsg.class, content);
//						case 2:
//							return DecodeMsg(EnterWorldMsg.class, content);
//					}
//					break;
//				case 1:
//					switch (mid)
//					{
//						case 2:
//							return DecodeMsg(EnterSceneMsg.class, content);
//					}
//					break;
//				case 37:
//					if (mid >= 2 && mid <= 5)
//						return new SecurityMsg();
//					else if (mid >= 20 && mid <= 25)
//						return new SHA1Msg();
//					else if (mid == 1)
//						return DecodeMsg(EchoMsg.class, content);
//					break;
//				default:
//					break;
//			}
		}
		return null;
	}

	private <T> T DecodeMsg(Class<T> clazz, ByteBuf content)
	{
		T msg = null;
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

	private <T> T DecodeObject(Class<T> clazz, ByteBuf content) throws Exception
	{
		T obj = clazz.newInstance();
		Field[] fs = clazz.getFields();        // 获取所有 PUBLIC 变量，包含自己定义的和继承的
		for (Field f : fs)
			DecodeField(obj, f, content);
		return obj;
	}

	@SuppressWarnings("unchecked")
	private <T> T DecodePrimitive(Class<T> clazz, ByteBuf content) throws Exception
	{
		if (clazz == boolean.class)
			return (T) Boolean.valueOf(content.readBoolean());
		else if (clazz == byte.class)
			return (T) Byte.valueOf(content.readByte());
		else if (clazz == char.class)
			return (T) Character.valueOf(content.readChar());
		else if (clazz == short.class)
			return (T) Short.valueOf(content.readShort());
		else if (clazz == int.class)
			return (T) Integer.valueOf(content.readInt());
		else if (clazz == long.class)
			return (T) Long.valueOf(content.readLong());
		else if (clazz == float.class)
			return (T) Float.valueOf(content.readFloat());
		else if (clazz == double.class)
			return (T) Double.valueOf(content.readDouble());
		else if (clazz == void.class)
			return null;
		else
			return null;
	}

	private <T> void DecodeField(T obj, Field field, ByteBuf content) throws Exception
	{
		try
		{
			Class<?> fieldClazz = field.getType();
			if (fieldClazz.isPrimitive() || fieldClazz == String.class)
				DecodePrimitiveField(obj, field, content);
			else if (fieldClazz.isArray())
				DecodeArrayField(obj, field, content);
			else if (fieldClazz.isEnum())
				throw new UnsupportMessageFieldException(obj, field.getName());
			else
				DecodeCustomField(obj, field, content);
		}
		catch (MessageDeserializeException | UnsupportMessageFieldException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new MessageDeserializeException(obj, field.getName(), ex);
		}
	}

	private <T> void DecodePrimitiveField(T obj, Field field, ByteBuf content) throws Exception
	{
		Class<?> fieldClazz = field.getType();
		if (fieldClazz == boolean.class)
			field.setBoolean(obj, content.readBoolean());
		else if (fieldClazz == byte.class)
			field.setByte(obj, content.readByte());
		else if (fieldClazz == char.class)
			field.setChar(obj, content.readChar());
		else if (fieldClazz == short.class)
			field.setShort(obj, content.readShort());
		else if (fieldClazz == int.class)
			field.setInt(obj, content.readInt());
		else if (fieldClazz == long.class)
			field.setLong(obj, content.readLong());
		else if (fieldClazz == float.class)
			field.setFloat(obj, content.readFloat());
		else if (fieldClazz == double.class)
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

	private <T> void DecodeArrayField(T obj, Field field, ByteBuf content) throws Exception
	{
		Class<?> elemClazz = field.getType().getComponentType();
		short len = content.readShort();
		Object newArray = Array.newInstance(elemClazz, len);
		if(elemClazz.isPrimitive())
		{
			for (int i = 0; i < len; ++i)
			{
				Object elem = DecodePrimitive(elemClazz, content);
				if (elem == null)
					throw new MessageDeserializeException(obj, field.getName());
				Array.set(newArray, i, elem);
			}
		}
		else
		{
			for (int i = 0; i < len; ++i)
			{
				Object elem = DecodeObject(elemClazz, content);
				if (elem == null)
					throw new MessageDeserializeException(obj, field.getName());
				Array.set(newArray, i, elem);
			}
		}
		field.set(obj, newArray);
	}

	private <T> void DecodeCustomField(T obj, Field field, ByteBuf content) throws Exception
	{
		Object fieldObj = DecodeObject(field.getType(), content);
		if (fieldObj == null)
			throw new MessageDeserializeException(obj, field.getName());
		field.set(obj, fieldObj);
	}
}