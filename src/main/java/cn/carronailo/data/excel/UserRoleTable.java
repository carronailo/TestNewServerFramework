package cn.carronailo.data.excel;

import cn.carronailo.framework.data.DataSource;
import cn.carronailo.framework.data.DataType;

@DataSource(type = DataType.EXCEL, file = "用户角色表.xls", category = "User")
public class UserRoleTable
{
	public long roleID;
	public String userName;
}
