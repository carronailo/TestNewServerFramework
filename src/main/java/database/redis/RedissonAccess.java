package database.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;

import java.util.Calendar;

/**
 * Created by CarroNailo on 2017/5/15.
 */
public class RedissonAccess
{

	public static void main(String[] args)
	{
		Config redisConfig = new Config();
		redisConfig.setCodec(new FstCodec()).useSingleServer().setAddress("127.0.0.1:6379").setPassword("1qaz2wsx").setDatabase(1);
		RedissonClient client = Redisson.create(redisConfig);
//		List<Object> res = client.getList("mlist");
//		for(Object o : res)
//		{
//			System.out.println(o);
//		}
		TestUser user1 = new TestUser();
		user1.roleID = System.currentTimeMillis();
		user1.nickName = "carronailo";
		user1.level = 1;
		user1.vipLevel = 7;
		user1.gold = 1000;
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(2017, 5, 15, 19, 00, 00);
		user1.lastLogin = c.getTime();
//		RBucket<database.redis.TestUser> bucket = client.getBucket("lijing1");
//		bucket.set(user1);
		RBucket<TestUser> bucket = client.getBucket("lijing1");
		TestUser user = bucket.get();
		System.out.println(user.toString());
		client.shutdown();
	}
}
