import common.utility.Pair;
import data.config.excel.ConfigReader;
import data.config.excel.tables.ConfigTableMap;
import data.config.excel.tables.UserRoleTable;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import net.netty.InternalClientHandler;
import net.netty.MultiClient;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.text.SimpleDateFormat;
import java.util.*;

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

		ConfigReader reader = new ConfigReader("resources/");
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
			InternalClientHandler.roleList.add(userRole.roleid);
		}

		MultiClient client = new MultiClient();
		Bootstrap bootstrap = client.PrepareBootstrap(clientNumber);
		if (bootstrap == null)
			return;

		System.out.println("关闭控制台常规输出");
		System.out.close();

		List<Future<?>> fs = new ArrayList<>();
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
					fs.add(client.StartClient(i, host, port, bootstrap));
				}
			}
		});

		while (!workFuture.isDone())
		{
			try
			{
				System.err.println(String.format("%s: 结束[n: %d, e: %d, u: %d]", timeOnlyFormatter.format(new Date()),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
				System.err.println(String.format("\t\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
				System.err.println(String.format("\t\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
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

		boolean allDone = false;
		while (!allDone)
		{
			allDone = true;
			for (Future<?> f : fs)
			{
				if (!f.isDone())
				{
					allDone = false;
					break;
				}
			}
			try
			{
				System.err.println(String.format("%s: 结束[n: %d, e: %d, u: %d]", timeOnlyFormatter.format(new Date()),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
				System.err.println(String.format("\t\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
				System.err.println(String.format("\t\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
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
		System.err.println(String.format("%s: 结束[n: %d, e: %d, u: %d]", timeOnlyFormatter.format(new Date()),
			MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
		System.err.println(String.format("\t\t\t登录[t: %d, s: %d, f: %d], 副本[t: %d, c: %d, s: %d, f: %d], 竞技场[t: %d, c: %d, s: %d, f: %d], 世界Boss[t: %d, c: %d, s: %d, f: %d]",
			MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
			MultiClient.copyTryCount.get(), MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
			MultiClient.arenaTryCount.get(), MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
			MultiClient.worldBossTryCount.get(), MultiClient.worldBossChallengeCount.get(), MultiClient.worldBossSuccessCount.get(), MultiClient.worldBossFailCount.get()));
		System.err.println(String.format("\t\t\t爬塔[t: %d, c: %d, s: %d, f: %d], 守护洛羽[t: %d, c: %d, s: %d, f: %d], 远征[t: %d, c: %d, s: %d, f: %d], 夺宝[t: %d, c: %d, s: %d, f: %d]",
			MultiClient.towerUpTryCount.get(), MultiClient.towerUpChallengeCount.get(), MultiClient.towerUpSuccessCount.get(), MultiClient.towerUpFailCount.get(),
			MultiClient.guardNPCTryCount.get(), MultiClient.guardNPCChallengeCount.get(), MultiClient.guardNPCSuccessCount.get(), MultiClient.guardNPCFailCount.get(),
			MultiClient.expeditionTryCount.get(), MultiClient.expeditionChallengeCount.get(), MultiClient.expeditionSuccessCount.get(), MultiClient.expeditionFailCount.get(),
			MultiClient.treasureRoadTryCount.get(), MultiClient.treasureRoadChallengeCount.get(), MultiClient.treasureRoadSuccessCount.get(), MultiClient.treasureRoadFailCount.get()));
		bootstrap.config().group().shutdownGracefully();

	}
}
