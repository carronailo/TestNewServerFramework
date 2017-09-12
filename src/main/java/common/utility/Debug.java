package common.utility;

import common.exceptions.RuntimeAssertException;

/**
 * Created by CarroNailo on 2017/9/12 10:59 for TestNewServerFramework.
 */
public class Debug
{
	public static void Assert(boolean exp, String msg) throws RuntimeAssertException
	{
		if(!exp)
		{
			assert(false);
			throw new RuntimeAssertException(msg);
		}
	}

	public static void Assert(boolean exp, Throwable e) throws RuntimeAssertException
	{
		if(!exp)
		{
			assert(exp);
			throw new RuntimeAssertException("assert fail", e);
		}
	}
}
