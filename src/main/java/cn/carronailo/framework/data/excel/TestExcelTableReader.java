package cn.carronailo.framework.data.excel;

import cn.carronailo.data.excel.UserRoleTable;
import cn.carronailo.framework.data.excel.tables.ConfigTableMap;

/**
 * Created by CarroNailo on 2017/10/13 16:45 for TestNewServerFramework.
 */
public class TestExcelTableReader
{
	public static void main(String[] args)
	{
//		ConfigTableMap.getInstance();

		ExcelTableReader reader = new ExcelTableReader("resources/");
//		reader.readAll();
		UserRoleTable[] userRoleTableContent = reader.getConfig(UserRoleTable.class);
		for(UserRoleTable userRole : userRoleTableContent)
		{
			System.out.println(String.format("username:%s, roleID:%d", userRole.userName, userRole.roleID));
		}
	}

}
