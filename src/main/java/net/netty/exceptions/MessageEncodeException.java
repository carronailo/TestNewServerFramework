package net.netty.exceptions;

public class MessageEncodeException extends Exception
{
	public MessageEncodeException(Object msgObj, String fieldName)
	{
		super(String.format("消息编码错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName));
	}

	public MessageEncodeException(Object msgObj, String fieldName, Throwable cause)
	{
		super(String.format("消息编码错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
