package data.config.excel.tables;

import data.config.CONF;
import data.config.TYPE;

@CONF(TYPE = TYPE.EXCEL, FILE = "用户角色表.xls", INFO = "User")
public class UserRoleTable
{
	public long roleid;
	public String userName;
}
