package database.redis;

import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

		client.getAtomicLong("lijing");

		RBucket<database.redis.TestUser> bucket = client.getBucket("user1");
		TestUser user1 = bucket.get();
		if (user1 == null)
		{
			System.out.println("create new TestUser");
			user1 = new TestUser();
			user1.roleID = System.currentTimeMillis();
			user1.nickName = "carronailo";
			user1.level = 1;
			user1.vipLevel = 7;
			user1.gold = 1000;
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			c.set(2017, 5, 15, 19, 00, 00);
			user1.lastLogin = c.getTime();
			user1.attributeMap = new HashMap<>();
			user1.attributeMap.put(TestUser.ATTR_STR, 10.0);
			user1.attributeMap.put(TestUser.ATTR_DEX, 10.0);
			user1.attributeMap.put(TestUser.ATTR_INT, 10.0);
			user1.attributeMap.put(TestUser.ATTR_ATT, 200.0);
			user1.attributeMap.put(TestUser.ATTR_DEF, 50.0);
			user1.attributeMap.put(TestUser.ATTR_HP, 2133.0);
			user1.attributeMap.put(TestUser.ATTR_MP, 73.0);
			user1.friendList = new ArrayList<>();
			user1.friendList.add(1234567890L);
			user1.friendList.add(98764315649852L);
			user1.friendList.add(87543544966963L);
			bucket.set(user1);
		}
		RBucket strBucket = client.getBucket("user1");
		Object user1Str = strBucket.get();

		System.out.println(user1);
		System.out.println(user1Str);

		client.shutdown();

//		List<Object> res = client.getList("mlist");
//		for(Object o : res)
//		{
//			System.out.println(o);
//		}
//		RBucket<database.database.TestUser> bucket = client.getBucket("lijing1");
//		bucket.set(user1);
//		RBucket<TestUser> bucket = client.getBucket("lijing1");
//		TestUser user = bucket.get();
//		System.out.println(user.toString());
//		client.shutdown();
	}
}
