package database.redis;

import redis.clients.jedis.Jedis;

import java.util.List;

public class JedisAccess
{
	public static void main(String[] args)
	{
		Jedis jedis;
		jedis = new Jedis("127.0.0.1", 6379);
		//jedis.auth("admin");

		String res = jedis.get("lijing");
		System.out.println(res);

		List<String> listRes = jedis.lrange("mlist", 0, -1);
		for(String s : listRes)
			System.out.println(s);
	}

}
