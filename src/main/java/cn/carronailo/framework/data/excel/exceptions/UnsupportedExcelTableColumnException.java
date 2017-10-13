package cn.carronailo.framework.data.excel.exceptions;

public class UnsupportedExcelTableColumnException extends Exception
{
	public UnsupportedExcelTableColumnException(Object configObj, String fieldName)
	{
		super(String.format("不支持的字段 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName));
	}

	public UnsupportedExcelTableColumnException(Object configObj, String fieldName, Throwable cause)
	{
		super(String.format("不支持的字段 %s@%s", configObj != null ? configObj.getClass().getSimpleName() : "null", fieldName), cause);
	}

}
