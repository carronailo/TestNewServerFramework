package net.netty.messages;

/**
 * Created by CarroNailo on 2017/9/8 17:02 for TestNewServerFramework.
 */
public class OutBoundMessageMap extends MessageMap
{
	private static OutBoundMessageMap instance = null;

	public static OutBoundMessageMap getInstance()
	{
		if(instance == null)
		{
			synchronized (OutBoundMessageMap.class)
			{
				if(instance == null)
				{
					instance = new OutBoundMessageMap();
					instance.initMessageMap();
				}
			}
		}
		return instance;
	}

	public OutBoundMessageMap()
	{
		packageName = InBoundMessageMap.class.getPackage().getName().concat(".outbound");
	}
}
