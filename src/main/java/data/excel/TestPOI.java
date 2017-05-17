package data.excel;

import java.io.File;

/**
 * Created by CarroNailo on 2017/5/16.
 */
public class TestPOI
{
	public static void main(String[] args)
	{
		File excelFile = new File("怪物属性表.xls");
		System.out.println(excelFile.getAbsolutePath());
		System.out.println(excelFile.exists());
	}
}
