package cn.carronailo.framework.data.excel.exceptions;

public class ExcelTableNotExistsException extends Exception
{
	public ExcelTableNotExistsException(String configTable)
	{
		super(String.format("配置表[%s]不存在", configTable));
	}

	public ExcelTableNotExistsException(String configTable, Throwable cause)
	{
		super(String.format("配置表[%s]不存在", configTable), cause);
	}
}
