package net.netty.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<Integer, Map<Integer, Class>> messageMap = null;

	private InBoundMessageMap()
	{
		packageName = InBoundMessageMap.class.getPackage().getName().concat(".inbound");
	}

	public void initMessageMap()
	{
		if(initialized)
			return;

		try
		{
			messageMap = new HashMap<>();

			if(!packageName.isEmpty())
			{
				List<String> inboundMessageNames = doScan(packageName, new ArrayList<>());
				for(String inboundMessage : inboundMessageNames)
				{
					Class<?> c = Class.forName(inboundMessage);
					RPC rpcAnnotation = c.getAnnotation(RPC.class);
					if(rpcAnnotation != null && rpcAnnotation.CID() >= 0 && rpcAnnotation.MID() >= 0)
					{
						Map<Integer, Class> tmp = messageMap.computeIfAbsent(rpcAnnotation.CID(), m -> new HashMap<>());
						tmp.put(rpcAnnotation.MID(), c);
					}
				}
			}

			initialized = true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public Class getMessageClassByID(int cid, int mid)
	{
		if (!initialized)
			initMessageMap();
		Map<Integer, Class> tmp = messageMap.get(cid);
		if (tmp != null)
			return tmp.get(mid);
		return null;
	}

}
