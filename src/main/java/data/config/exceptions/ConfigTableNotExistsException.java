package data.config.exceptions;

import net.bytebuddy.implementation.bytecode.Throw;

public class ConfigTableNotExistsException extends Exception
{
	public ConfigTableNotExistsException(String configTable)
	{
		super(String.format("配置表[%s]不存在", configTable));
	}

	public ConfigTableNotExistsException(String configTable, Throwable cause)
	{
		super(String.format("配置表[%s]不存在", configTable), cause);
	}
}
