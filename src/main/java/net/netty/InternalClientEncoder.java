package net.netty;

import common.utility.Pair;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.netty.exceptions.MessageDecodeException;
import net.netty.exceptions.MessageEncodeException;
import net.netty.exceptions.UnsupportMessageFieldException;
import net.netty.messages.OutBoundMessageMap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * Created by CarroNailo on 2017/9/8 16:17 for TestNewServerFramework.
 */
public class InternalClientEncoder extends MessageToByteEncoder
{
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
	{
		Pair<Integer, Integer> idPair = OutBoundMessageMap.getInstance().getIDByMessageClass(msg.getClass());
		if (idPair != null)
		{
			int startIndex = out.writerIndex();
			out.ensureWritable(4);
			out.writeByte(0);
			out.writeByte(0);
			out.writeByte(idPair.first);
			out.writeByte(idPair.second);
			int msgLen = EncodeMsg(msg, out);
			int endIndex = out.writerIndex();
			out.writerIndex(startIndex);
			out.writeShort(msgLen + 2);
			out.writerIndex(endIndex);
		}
	}

	private int EncodeMsg(Object msgObj, ByteBuf out)
	{
		int msgLen = 0;
		try
		{
			msgLen = EncodeObject(msgObj, out);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return msgLen;
	}

	private int EncodeObject(Object obj, ByteBuf out) throws Exception
	{
		int len = 0;
		Class<?> clazz = obj.getClass();
		Field[] fs = clazz.getFields();        // 获取所有 PUBLIC 变量，包含自己定义的和继承的
		for (Field f : fs)
			len += EncodeField(obj, f, out);
		return len;
	}

	private int EncodePrimitive(Object value, ByteBuf out) throws Exception
	{
		int len = 0;
		Class<?> clazz = value.getClass();
		if (clazz == boolean.class || clazz == Boolean.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeBoolean((boolean)value);
		}
		else if (clazz == byte.class || clazz == Byte.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeByte((Byte)value);
		}
		else if (clazz == char.class || clazz == Character.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeChar((Character)value);
		}
		else if (clazz == short.class || clazz == Short.class)
		{
			len = 2;
			out.ensureWritable(len);
			out.writeShort((Short)value);
		}
		else if (clazz == int.class || clazz == Integer.class)
		{
			len = 4;
			out.ensureWritable(len);
			out.writeInt((Integer)value);
		}
		else if (clazz == long.class || clazz == Long.class)
		{
			len = 8;
			out.ensureWritable(len);
			out.writeLong((Long)value);
		}
		else if (clazz == float.class || clazz == Float.class)
		{
			len = 4;
			out.ensureWritable(len);
			out.writeFloat((Float)value);
		}
		else if (clazz == double.class || clazz == Double.class)
		{
			len = 8;
			out.ensureWritable(len);
			out.writeDouble((Double)value);
		}
		return len;
	}

	private int EncodeField(Object obj, Field field, ByteBuf out) throws Exception
	{
		int len = 0;
		try
		{
			Class<?> fieldClazz = field.getType();
			if (fieldClazz.isPrimitive() || fieldClazz == String.class)
				len += EncodePrimitiveField(obj, field, out);
			else if (fieldClazz.isArray())
				len += EncodeArrayField(obj, field, out);
			else if (fieldClazz.isEnum())
				throw new UnsupportMessageFieldException(obj, field.getName());
			else
				len += EncodeCustomField(obj, field, out);
		}
		catch (MessageEncodeException | UnsupportMessageFieldException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new MessageEncodeException(obj, field.getName(), ex);
		}
		return len;
	}

	private int EncodePrimitiveField(Object obj, Field field, ByteBuf out) throws Exception
	{
		int len;
		Class<?> fieldClazz = field.getType();
		if (fieldClazz == boolean.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeBoolean(field.getBoolean(obj));
		}
		else if (fieldClazz == byte.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeByte(field.getByte(obj));
		}
		else if (fieldClazz == char.class)
		{
			len = 1;
			out.ensureWritable(len);
			out.writeChar(field.getChar(obj));
		}
		else if (fieldClazz == short.class)
		{
			len = 2;
			out.ensureWritable(len);
			out.writeShort(field.getShort(obj));
		}
		else if (fieldClazz == int.class)
		{
			len = 4;
			out.ensureWritable(len);
			out.writeInt(field.getInt(obj));
		}
		else if (fieldClazz == long.class)
		{
			len = 8;
			out.ensureWritable(len);
			out.writeLong(field.getLong(obj));
		}
		else if (fieldClazz == float.class)
		{
			len = 4;
			out.ensureWritable(len);
			out.writeFloat(field.getFloat(obj));
		}
		else if (fieldClazz == double.class)
		{
			len = 8;
			out.ensureWritable(len);
			out.writeDouble(field.getDouble(obj));
		}
		else if (fieldClazz == void.class)
			throw new UnsupportMessageFieldException(obj, field.getName());
		else if (fieldClazz == String.class)
		{
			byte[] bytes = ((String)field.get(obj)).getBytes("UTF8");
			len = 2 + bytes.length;
			out.ensureWritable(len);
			out.writeShort(bytes.length);
			out.writeBytes(bytes);
		}
		else
			throw new UnsupportMessageFieldException(obj, field.getName());
		return len;
	}

	private int EncodeArrayField(Object obj, Field field, ByteBuf out) throws Exception
	{
		int len = 0;
		Class<?> elemClazz = field.getType().getComponentType();
		Object array = field.get(obj);
		int arraySize = Array.getLength(array);
		len += 2;
		out.ensureWritable(len);
		out.writeShort(arraySize);
		if(elemClazz.isPrimitive())
		{
			for (int i = 0; i < arraySize; ++i)
			{
				Object elem = Array.get(array, i);
				if (elem == null)
					throw new MessageDecodeException(obj, field.getName());
				len += EncodePrimitive(elem, out);
			}
		}
		else
		{
			for (int i = 0; i < arraySize; ++i)
			{
				Object elem = Array.get(array, i);
				if (elem == null)
					throw new MessageDecodeException(obj, field.getName());
				len += EncodeObject(elem, out);
			}
		}
		return len;
	}

	private int EncodeCustomField(Object obj, Field field, ByteBuf out) throws Exception
	{
		return EncodeObject(field.get(obj), out);
	}
}
