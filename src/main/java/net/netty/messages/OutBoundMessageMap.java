package net.netty.messages;

import common.utility.PackageScanner;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CarroNailo on 2017/9/8 17:02 for TestNewServerFramework.
 */
public class OutBoundMessageMap
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

	private Map<Class, Pair<Integer, Integer>> messageMap = null;

	private String packageName = "";

	private volatile boolean initialized = false;

	private OutBoundMessageMap()
	{
		packageName = InBoundMessageMap.class.getPackage().getName().concat(".outbound");
	}

	private void initMessageMap()
	{
		if(initialized)
			return;

		try
		{
			messageMap = new HashMap<>();

			if(!packageName.isEmpty())
			{
				List<String> outboundMessageNames = PackageScanner.doScan(packageName, this.getClass().getClassLoader(), new ArrayList<>());
				for(String outboundMessage : outboundMessageNames)
				{
					Class<?> c = Class.forName(outboundMessage);
					RPC rpcAnnotation = c.getAnnotation(RPC.class);
					if(rpcAnnotation != null && rpcAnnotation.CID() >= 0 && rpcAnnotation.MID() >= 0)
					{
						messageMap.put(c, new Pair<>(rpcAnnotation.CID(), rpcAnnotation.MID()));
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

	public Pair<Integer, Integer> getIDByMessageClass(Class<?> msgClazz)
	{
		if (!initialized)
			initMessageMap();
		return messageMap.get(msgClazz);
	}

}
