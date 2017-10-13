package cn.carronailo.framework.data.excel.tables;

import cn.carronailo.framework.data.DataType;
import common.utility.PackageScanner;
import cn.carronailo.framework.data.DataSource;

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
					DataSource dataSourceAnnotation = c.getAnnotation(DataSource.class);
					if(dataSourceAnnotation != null && dataSourceAnnotation.type() == DataType.EXCEL
						&& !dataSourceAnnotation.file().isEmpty() && !dataSourceAnnotation.category().isEmpty())
					{
						Map<String, Class> tmp = configTableMap.computeIfAbsent(dataSourceAnnotation.file(), m -> new HashMap<>());
						tmp.put(dataSourceAnnotation.category(), c);
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
