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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

		final int clientNumber;
		if(args.length > 0)
			clientNumber = Integer.parseInt(args[0]);
		else
			clientNumber = 1;

		final int stepMilli;
		if(args.length > 1)
			stepMilli = Integer.parseInt(args[1]);
		else
			stepMilli = 20;

		for(UserRoleTable userRole : userRoleTableContent)
		{
			if(!userRole.userName.isEmpty())
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
					fs.add(host.StartClient(i, "172.31.32.12", 6868, bootstrap));		// 内网IP
//					fs.add(host.StartClient(i, "127.0.0.1", 6868, bootstrap));
				}
			}
		});

		while(!workFuture.isDone())
		{
			try
			{
				System.err.println(String.format("%s: 登录[s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d]",
					timeOnlyFormatter.format(new Date()),
					MultiClient.loginSuccessCount, MultiClient.loginFailCount,
					MultiClient.copyChallengeCount, MultiClient.copySuccessCount, MultiClient.copyFailCount,
					MultiClient.arenaChallengeCount, MultiClient.arenaSuccessCount, MultiClient.arenaFailCount,
					MultiClient.normalFinishCount, MultiClient.errorFinishCount));
				Thread.sleep(1000);
			}
			catch(Exception ignored)
			{
			}
		}
		workGroup.shutdownGracefully();

		boolean allDone = false;
		while(!allDone)
		{
			allDone = true;
			for (Future<?> f : fs)
			{
				if(!f.isDone())
				{
					allDone = false;
					break;
				}
			}
			try
			{
				System.err.println(String.format("%s: 登录[s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d]",
					timeOnlyFormatter.format(new Date()),
					MultiClient.loginSuccessCount, MultiClient.loginFailCount,
					MultiClient.copyChallengeCount, MultiClient.copySuccessCount, MultiClient.copyFailCount,
					MultiClient.arenaChallengeCount, MultiClient.arenaSuccessCount, MultiClient.arenaFailCount,
					MultiClient.normalFinishCount, MultiClient.errorFinishCount));
				Thread.sleep(1000);
			}
			catch(Exception ignored)
			{
			}
		}
		System.err.println(String.format("%s: 登录[s: %d, f: %d], 副本[c: %d, s: %d, f: %d], 竞技场[c: %d, s: %d, f: %d], 结束[n: %d, e: %d]",
			timeOnlyFormatter.format(new Date()),
			MultiClient.loginSuccessCount, MultiClient.loginFailCount,
			MultiClient.copyChallengeCount, MultiClient.copySuccessCount, MultiClient.copyFailCount,
			MultiClient.arenaChallengeCount, MultiClient.arenaSuccessCount, MultiClient.arenaFailCount,
			MultiClient.normalFinishCount, MultiClient.errorFinishCount));
		bootstrap.config().group().shutdownGracefully();
	}
}