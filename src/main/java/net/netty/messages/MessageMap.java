package net.netty.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Created by CarroNailo on 2017/9/8 17:02 for TestNewServerFramework.
 */
public abstract class MessageMap
{
	String packageName = "";

	volatile boolean initialized = false;

	/**
	 * Actually perform the scanning procedure.
	 *
	 * @param basePackage
	 * @param nameList    A list to contain the result.
	 * @return A list of fully qualified names.
	 * @throws IOException
	 */
	List<String> doScan(String basePackage, List<String> nameList) throws IOException
	{
		// replace dots with splashes
		String splashPath = dotToSplash(basePackage);

		List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"

		// get file path
		URL url = this.getClass().getClassLoader().getResource(splashPath);
		if(url != null)
		{
			String filePath = getRootPath(url);
			// Get classes in that package.
			// If the web server unzips the jar file, then the classes will exist in the form of
			// normal file in the directory.
			// If the web server does not unzip the jar file, then classes will exist in jar file.
			if (isJarFile(filePath))
				names = readFromJarFile(filePath, splashPath);
			else
				names = readFromDirectory(filePath);
		}

		if(names != null)
		{
			for (String name : names)
			{
				if (isClassFile(name))
				{
					//nameList.add(basePackage + "." + StringUtil.trimExtension(name));
					nameList.add(toFullyQualifiedName(name, basePackage));
				}
				else
				{
					// this is a directory
					// check this directory for more classes
					// do recursive invocation
					doScan(basePackage + "." + name, nameList);
				}
			}
		}

		return nameList;
	}

	/**
	 * Convert short class name to fully qualified name.
	 * e.g., String -> java.lang.String
	 */
	private String toFullyQualifiedName(String shortName, String basePackage)
	{
		return basePackage.concat(".").concat(trimExtension(shortName));
	}

	private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException
	{
		JarInputStream jarIn = new JarInputStream(new FileInputStream(jarPath));
		JarEntry entry = jarIn.getNextJarEntry();

		List<String> nameList = new ArrayList<>();
		while (null != entry)
		{
			String name = entry.getName();
			if (name.startsWith(splashedPackageName) && isClassFile(name))
				nameList.add(name);
			entry = jarIn.getNextJarEntry();
		}

		return nameList;
	}

	private List<String> readFromDirectory(String path)
	{
		File file = new File(path);
		String[] names = file.list();

		if (null == names)
			return null;

		return Arrays.asList(names);
	}

	private boolean isClassFile(String name)
	{
		return name.endsWith(".class");
	}

	private boolean isJarFile(String name)
	{
		return name.endsWith(".jar");
	}

	/**
	 * "file:/home/whf/cn/fh" -> "/home/whf/cn/fh"
	 * "jar:file:/home/whf/foo.jar!cn/fh" -> "/home/whf/foo.jar"
	 */
	private String getRootPath(URL url)
	{
		String fileUrl = url.getFile();
		int pos = fileUrl.indexOf('!');

		if (-1 == pos)
		{
			return fileUrl;
		}

		return fileUrl.substring(5, pos);
	}

	/**
	 * "cn.fh.lightning" -> "cn/fh/lightning"
	 *
	 * @param name
	 * @return
	 */
	private String dotToSplash(String name)
	{
		return name.replaceAll("\\.", "/");
	}

	/**
	 * "Apple.class" -> "Apple"
	 */
	private String trimExtension(String name)
	{
		int pos = name.indexOf('.');
		if (-1 != pos)
		{
			return name.substring(0, pos);
		}

		return name;
	}

	/**
	 * /application/home -> /home
	 *
	 * @param uri
	 * @return
	 */
	private String trimURI(String uri)
	{
		String trimmed = uri.substring(1);
		int splashIndex = trimmed.indexOf('/');

		return trimmed.substring(splashIndex);
	}

}
