package data.config.excel.tables;

import common.utility.PackageScanner;
import data.config.CONF;
import data.config.TYPE;

import java.util.*;

public class ConfigTableMap
{
	private static ConfigTableMap instance = null;

	public static ConfigTableMap getInstance()
	{
		if(instance == null)
		{
			synchronized (ConfigTableMap.class)
			{
				if(instance == null)
				{
					instance = new ConfigTableMap();
					instance.initConfigTableMap();
				}
			}
		}
		return instance;
	}

	private Map<String, Map<String, Class>> configTableMap = null;

	private String packageName = "";

	private volatile boolean initialized = false;

	private ConfigTableMap()
	{
		packageName = ConfigTableMap.class.getPackage().getName();
	}

	private void initConfigTableMap()
	{
		if(initialized)
			return;

		try
		{
			configTableMap = new HashMap<>();

			if(!packageName.isEmpty())
			{
				List<String> configTableNames = PackageScanner.doScan(packageName, this.getClass().getClassLoader(), new ArrayList<>());
				for(String configTable : configTableNames)
				{
					Class<?> c = Class.forName(configTable);
					CONF confAnnotation = c.getAnnotation(CONF.class);
					if(confAnnotation != null && confAnnotation.TYPE() == TYPE.EXCEL
						&& !confAnnotation.FILE().isEmpty() && !confAnnotation.INFO().isEmpty())
					{
						Map<String, Class> tmp = configTableMap.computeIfAbsent(confAnnotation.FILE(), m -> new HashMap<>());
						tmp.put(confAnnotation.INFO(), c);
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

	public Class getConfigTableClassByFile(String file, String sheet)
	{
		if (!initialized)
			initConfigTableMap();

		Map<String, Class> tmp = configTableMap.get(file);
		if (tmp != null)
			return tmp.get(sheet);
		return null;
	}

	public Iterator<Map.Entry<String, Map<String, Class>>> getAll()
	{
		if (!initialized)
			initConfigTableMap();

		return configTableMap.entrySet().iterator();
	}
}
