package database.redis;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by CarroNailo on 2017/5/15.
 */
public class TestUser implements Serializable
{
	public long roleID;
	public String nickName;
	public int level;
	public int vipLevel;
	public int gold;
	public Date lastLogin;

	@Override
	public String toString()
	{
		return "" + roleID + " " + nickName + " " + level + " " + vipLevel + " " + gold + " " + lastLogin.toString();
	}

}
