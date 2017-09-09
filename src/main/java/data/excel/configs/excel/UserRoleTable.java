package data.excel.configs.excel;

import data.excel.configs.CONF;
import data.excel.configs.TYPE;

@CONF(TYPE = TYPE.EXCEL, FILE = "用户角色表.xls", INFO = "User")
public class UserRoleTable
{
	public long roleid;
	public String userName;
}
