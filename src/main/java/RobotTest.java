import common.utility.Pair;
import cn.carronailo.framework.data.excel.ExcelTableReader;
import cn.carronailo.framework.data.excel.tables.ConfigTableMap;
import cn.carronailo.data.excel.UserRoleTable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import net.netty.InternalClientHandler;
import net.netty.MultiClient;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by CarroNailo on 2017/9/11 10:24 for TestNewServerFramework.
 */
public class RobotTest
{
	private static Random rand = new Random();
	private static SimpleDateFormat timeOnlyFormatter = new SimpleDateFormat("HH:mm:ss");

	public static void main(String[] args)
	{
		ConfigTableMap.getInstance();

		ExcelTableReader reader = new ExcelTableReader("resources/");
		UserRoleTable[] userRoleTableContent = reader.getConfig(UserRoleTable.class);

		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		if(args.length < 3)
		{
			System.out.println("Usage: java -jar [jar file] [client]:[port] [client number] [interval milliseconds]");
			return;
		}

		final String host = args[0].substring(0, args[0].indexOf(":"));
		final int port = Integer.parseInt(args[0].substring(args[0].indexOf(":") + 1));
		final int clientNumber = Integer.parseInt(args[1]);
		final int stepMilli = Integer.parseInt(args[2]);

		for (UserRoleTable userRole : userRoleTableContent)
		{
			if (!userRole.userName.isEmpty())
			{
				Pair<String, Integer> p = Pair.makePair(userRole.userName, 1002 + rand.nextInt(3));
				InternalClientHandler.userQueue.add(p);
			}
			InternalClientHandler.roleList.add(userRole.roleID);
		}

		MultiClient client = new MultiClient();
		Bootstrap bootstrap = client.PrepareBootstrap(clientNumber);
		if (bootstrap == null)
			return;

		System.out.println("关闭控制台常规输出");
		System.out.close();

		Map<Integer, Future<?>> fs = new ConcurrentHashMap<>();
		EventExecutorGroup workGroup = new DefaultEventExecutorGroup(1);
		Future<?> workFuture = workGroup.submit(new Runnable()
		{
			@Override
			public void run()
			{
				for (int i = 0; i < clientNumber; ++i)
				{
					try
					{
						Thread.sleep(stepMilli);
					}
					catch (Exception ignored)
					{
					}
//					fs.add(client.StartClient(i, "120.92.16.58", 6868, bootstrap));		// 外网IP
//					fs.add(client.StartClient(i, "172.31.32.12", 6868, bootstrap));        // 内网IP
//					fs.add(client.StartClient(i, "127.0.0.1", 6868, bootstrap));
					ChannelFuture f = client.StartClient(i, host, port, bootstrap);
					if(f == null)
						continue;
					fs.put(i, f);
					final int index = i;
					f.addListener(new ChannelFutureListener() {
						private int idx = index;
						@Override
						public void operationComplete(ChannelFuture future) throws Exception
						{
							fs.remove(idx);
						}
					});
				}
			}
		});

		while (!workFuture.isDone())
		{
			try
			{
				System.err.println(String.format("%s(1): 结束[n: %d, e: %d, u: %d, c: %d]", timeOnlyFormatter.format(new Date()),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get(), MultiClient.connectionCloseCount.get()));
				System.err.println(String.format("\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
				System.err.println(String.format("\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.towerUpTryCount.get(), MultiClient.towerUpChallengeCount.get(), MultiClient.towerUpSuccessCount.get(), MultiClient.towerUpFailCount.get(),
					MultiClient.guardNPCTryCount.get(), MultiClient.guardNPCChallengeCount.get(), MultiClient.guardNPCSuccessCount.get(), MultiClient.guardNPCFailCount.get(),
					MultiClient.expeditionTryCount.get(), MultiClient.expeditionChallengeCount.get(), MultiClient.expeditionSuccessCount.get(), MultiClient.expeditionFailCount.get(),
					MultiClient.treasureRoadTryCount.get(), MultiClient.treasureRoadChallengeCount.get(), MultiClient.treasureRoadSuccessCount.get(), MultiClient.treasureRoadFailCount.get()));
				Thread.sleep(1000);
			}
			catch (Exception ignored)
			{
			}
		}
		workGroup.shutdownGracefully();

		while (fs.size() > 0)
		{
			try
			{
				System.err.println(String.format("%s(2): 结束[n: %d, e: %d, u: %d, c: %d]", timeOnlyFormatter.format(new Date()),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get(), MultiClient.connectionCloseCount.get()));
				System.err.println(String.format("\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
				System.err.println(String.format("\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.towerUpTryCount.get(), MultiClient.towerUpChallengeCount.get(), MultiClient.towerUpSuccessCount.get(), MultiClient.towerUpFailCount.get(),
					MultiClient.guardNPCTryCount.get(), MultiClient.guardNPCChallengeCount.get(), MultiClient.guardNPCSuccessCount.get(), MultiClient.guardNPCFailCount.get(),
					MultiClient.expeditionTryCount.get(), MultiClient.expeditionChallengeCount.get(), MultiClient.expeditionSuccessCount.get(), MultiClient.expeditionFailCount.get(),
					MultiClient.treasureRoadTryCount.get(), MultiClient.treasureRoadChallengeCount.get(), MultiClient.treasureRoadSuccessCount.get(), MultiClient.treasureRoadFailCount.get()));
				Thread.sleep(1000);
			}
			catch (Exception ignored)
			{
			}
		}
		System.err.println(String.format("%s(3): 结束[n: %d, e: %d, u: %d, c: %d]", timeOnlyFormatter.format(new Date()),
			MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get(), MultiClient.connectionCloseCount.get()));
		System.err.println(String.format("\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
			MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
			MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
			MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
			MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
		System.err.println(String.format("\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
			MultiClient.towerUpTryCount.get(), MultiClient.towerUpChallengeCount.get(), MultiClient.towerUpSuccessCount.get(), MultiClient.towerUpFailCount.get(),
			MultiClient.guardNPCTryCount.get(), MultiClient.guardNPCChallengeCount.get(), MultiClient.guardNPCSuccessCount.get(), MultiClient.guardNPCFailCount.get(),
			MultiClient.expeditionTryCount.get(), MultiClient.expeditionChallengeCount.get(), MultiClient.expeditionSuccessCount.get(), MultiClient.expeditionFailCount.get(),
			MultiClient.treasureRoadTryCount.get(), MultiClient.treasureRoadChallengeCount.get(), MultiClient.treasureRoadSuccessCount.get(), MultiClient.treasureRoadFailCount.get()));
		bootstrap.config().group().shutdownGracefully();

	}
}
