package database.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

public class JedisAccess
{
	JedisPool pool = null;

	public JedisAccess()
	{
		JedisPoolConfig config = new JedisPoolConfig();
		config.setBlockWhenExhausted(false);
		config.setMaxTotal(16);
		config.setTestOnBorrow(true);
		pool = new JedisPool(config, "127.0.0.1", 6379, Protocol.DEFAULT_TIMEOUT, "1qaz2wsx");
	}

	public Jedis getJedis()
	{
		try
		{
			if(pool != null)
				return pool.getResource();
		}
		catch(JedisException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void returnJedis(Jedis jedis)
	{
		jedis.close();
	}


	public static void main(String[] args)
	{
		JedisAccess access = new JedisAccess();
		Jedis jedis = access.getJedis();
		if(jedis != null)
		{
			String res = jedis.get("lijing");
			System.out.println(res);

			List<String> listRes = jedis.lrange("mlist", 0, -1);
			for(String s : listRes)
				System.out.println(s);

			jedis.close();
		}
	}

}
