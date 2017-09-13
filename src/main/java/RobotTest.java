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

	private static class TestInteger implements Comparable<TestInteger>
	{
		public int value;
		public TestInteger(int value)
		{
			this.value = value;
		}

		@Override
		public int compareTo(TestInteger comp)
		{
			if(value < comp.value)
				return -1;
			else
				return 1;
		}
	}

	public static void main(String[] args)
	{
		ConfigTableMap.getInstance();

		ConfigReader reader = new ConfigReader("resources/");
		UserRoleTable[] userRoleTableContent = reader.getConfig(UserRoleTable.class);

		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		final int clientNumber;
		if (args.length > 0)
			clientNumber = Integer.parseInt(args[0]);
		else
			clientNumber = 10000;

		final int stepMilli;
		if (args.length > 1)
			stepMilli = Integer.parseInt(args[1]);
		else
			stepMilli = 20;

		for (UserRoleTable userRole : userRoleTableContent)
		{
			if (!userRole.userName.isEmpty())
			{
				Pair<String, Integer> p = Pair.makePair(userRole.userName, 1002 + rand.nextInt(3));
				InternalClientHandler.userQueue.add(p);
			}
			InternalClientHandler.roleList.add(userRole.roleid);
		}

		MultiClient host = new MultiClient();
		Bootstrap bootstrap = host.PrepareBootstrap(clientNumber);
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
//					fs.add(host.StartClient(i, "120.92.16.58", 6868, bootstrap));		// 外网IP
//					fs.add(host.StartClient(i, "172.31.32.12", 6868, bootstrap));        // 内网IP
					fs.add(host.StartClient(i, "127.0.0.1", 6868, bootstrap));
				}
			}
		});

		while (!workFuture.isDone())
		{
			try
			{
				System.err.println(String.format("%s: 登录[t: %d, s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d, u: %d]",
					timeOnlyFormatter.format(new Date()),
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
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
				System.err.println(String.format("%s: 登录[t: %d, s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d, u: %d]",
					timeOnlyFormatter.format(new Date()),
					MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
					MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
					MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
					MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
				Thread.sleep(1000);
			}
			catch (Exception ignored)
			{
			}
		}
		System.err.println(String.format("%s: 登录[t: %d, s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d, u: %d]",
			timeOnlyFormatter.format(new Date()),
			MultiClient.loginTryCount.get(), MultiClient.loginSuccessCount.get(), MultiClient.loginFailCount.get(),
			MultiClient.copyChallengeCount.get(), MultiClient.copySuccessCount.get(), MultiClient.copyFailCount.get(),
			MultiClient.arenaChallengeCount.get(), MultiClient.arenaSuccessCount.get(), MultiClient.arenaFailCount.get(),
			MultiClient.normalFinishCount.get(), MultiClient.errorFinishCount.get(), MultiClient.unexpectedFinishCount.get()));
		bootstrap.config().group().shutdownGracefully();

	}
}
