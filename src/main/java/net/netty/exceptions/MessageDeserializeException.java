package net.netty.exceptions;

/**
 * Created by CarroNailo on 2017/9/8 14:56 for TestNewServerFramework.
 */
public class MessageDeserializeException extends Exception
{
	public MessageDeserializeException(Object msgObj, String fieldName)
	{
		super(String.format("消息解析错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName));
	}

	public MessageDeserializeException(Object msgObj, String fieldName, Throwable cause)
	{
		super(String.format("消息解析错误 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
