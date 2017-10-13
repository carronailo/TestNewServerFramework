package cn.carronailo.framework.data.excel.exceptions;

public class ExcelTableCellParseException extends Exception
{
	public ExcelTableCellParseException(Object configObj, String fieldName)
	{
		super(String.format("配置解析错误 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName));
	}

	public ExcelTableCellParseException(Object configObj, String fieldName, Throwable cause)
	{
		super(String.format("配置解析错误 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName), cause);
	}
}
