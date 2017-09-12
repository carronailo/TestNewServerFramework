package common.exceptions;

/**
 * Created by CarroNailo on 2017/9/12 11:01 for TestNewServerFramework.
 */
public class RuntimeAssertException extends Exception
{
	public RuntimeAssertException(String msg)
	{
		super(msg);
	}

	public RuntimeAssertException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}
