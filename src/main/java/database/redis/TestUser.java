package database.redis;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by CarroNailo on 2017/5/15.
 */

public class TestUser implements Serializable
{
	public static final int ATTR_STR = 1;
	public static final int ATTR_DEX = 2;
	public static final int ATTR_INT = 3;
	public static final int ATTR_ATT = 4;
	public static final int ATTR_DEF = 5;
	public static final int ATTR_HP = 6;
	public static final int ATTR_MP = 7;

	public long roleID;
	public String nickName;
	public int level;
	public int vipLevel;
	public int gold;
	public Date lastLogin;
	public List<Long> friendList;
	public Map<Integer, Double> attributeMap;

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(roleID);
		sb.append(" ");
		sb.append(nickName);
		sb.append(" ");
		sb.append(level);
		sb.append(" ");
		sb.append(vipLevel);
		sb.append(" ");
		sb.append(gold);
		sb.append(" ");
		sb.append(lastLogin.toInstant());
		sb.append(" ");
		sb.append(" attr:");
		for(Map.Entry<Integer, Double> entry : attributeMap.entrySet())
		{
			sb.append(entry.getKey());
			sb.append("-");
			sb.append(entry.getValue());
			sb.append(",");
		}
		sb.append(" friends:");
		for(Long friend : friendList)
		{
			sb.append(friend.longValue());
			sb.append(",");
		}
		return sb.toString();
	}

}
