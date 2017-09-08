package net.netty.exceptions;

/**
 * Created by CarroNailo on 2017/9/8 15:08 for TestNewServerFramework.
 */
public class UnsupportMessageFieldException extends Exception
{
	public UnsupportMessageFieldException(Object msgObj, String fieldName)
	{
		super(String.format("不支持的类型 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName));
	}

	public UnsupportMessageFieldException(Object msgObj, String fieldName, Throwable cause)
	{
		super(String.format("不支持的类型 %s@%s", msgObj != null ? msgObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
