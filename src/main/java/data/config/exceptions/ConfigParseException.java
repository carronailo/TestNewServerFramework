package data.config.exceptions;

public class ConfigParseException extends Exception
{
	public ConfigParseException(Object configObj, String fieldName)
	{
		super(String.format("配置解析错误 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName));
	}

	public ConfigParseException(Object configObj, String fieldName, Throwable cause)
	{
		super(String.format("配置解析错误 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
