package net.netty.messages;

/**
 * Created by CarroNailo on 2017/9/8 16:38 for TestNewServerFramework.
 */
public class InBoundMessageMap extends MessageMap
{
	private static InBoundMessageMap instance = null;

	public static InBoundMessageMap getInstance()
	{
		if(instance == null)
		{
			synchronized (InBoundMessageMap.class)
			{
				if(instance == null)
				{
					instance = new InBoundMessageMap();
					instance.initMessageMap();
				}
			}
		}
		return instance;
	}

	private InBoundMessageMap()
	{
		packageName = InBoundMessageMap.class.getPackage().getName().concat(".inbound");
	}
}
