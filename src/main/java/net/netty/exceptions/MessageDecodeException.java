package net.netty.exceptions;

/**
 * Created by CarroNailo on 2017/9/8 14:56 for TestNewServerFramework.
 */
public class MessageDecodeException extends Exception
{
	public MessageDecodeException(Object msgObj, String fieldName)
	{
		super(String.format("消息解码错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName));
	}

	public MessageDecodeException(Object msgObj, String fieldName, Throwable cause)
	{
		super(String.format("消息解码错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
