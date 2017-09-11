import data.config.excel.ConfigReader;
import data.config.excel.tables.ConfigTableMap;
import data.config.excel.tables.UserRoleTable;
import io.netty.bootstrap.Bootstrap;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import javafx.util.Pair;
import net.netty.InternalClientHandler;
import net.netty.MultiClient;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by CarroNailo on 2017/9/11 10:24 for TestNewServerFramework.
 */
public class RobotTest
{
	private static Random rand = new Random();

	public static void main(String[] args)
	{

		ConfigTableMap.getInstance();

		ConfigReader reader = new ConfigReader("resources/");
		UserRoleTable[] userRoleTableContent = reader.getConfig(UserRoleTable.class);

		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		int clientNumber = 1;

		for(UserRoleTable userRole : userRoleTableContent)
		{
			if(!userRole.userName.isEmpty())
			{
				Pair<String, Integer> p =
					new Pair<>(userRole.userName, 1002 + rand.nextInt(3));
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
						Thread.sleep(50);
					}
					catch (Exception ignored)
					{
					}
					fs.add(host.StartClient(i, "120.92.16.58", 6868, bootstrap));
				}
			}
		});

		while(!workFuture.isDone())
		{
			try
			{
				System.err.println(String.format("登录成功[%d], 正常结束[%d], 异常结束[%d]", MultiClient.loginSuccessCount,
					MultiClient.normalFinishCount, MultiClient.errorFinishCount));
				Thread.sleep(1000);
			}
			catch(Exception ignored)
			{
			}
		}

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
				System.err.println(String.format("正常结束[%d], 异常结束[%d]", MultiClient.normalFinishCount, MultiClient.errorFinishCount));
				Thread.sleep(1000);
			}
			catch(Exception ignored)
			{
			}
		}
		bootstrap.config().group().shutdownGracefully();
	}
}
