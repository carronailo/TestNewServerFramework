package data.config.exceptions;

public class UnsupportConfigColumnException extends Exception
{
	public UnsupportConfigColumnException(Object configObj, String fieldName)
	{
		super(String.format("不支持的字段 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName));
	}

	public UnsupportConfigColumnException(Object configObj, String fieldName, Throwable cause)
	{
		super(String.format("不支持的字段 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName), cause);
	}

}
