package net.netty;

import common.utility.Pair;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.*;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by CarroNailo on 2017/9/6 10:31 for TestNewServerFramework.
 */
public class MultiClient
{
	public static AtomicInteger loginTryCount = new AtomicInteger(0);
	public static AtomicInteger loginSuccessCount = new AtomicInteger(0);
	public static AtomicInteger loginFailCount = new AtomicInteger(0);
	public static AtomicInteger copyTryCount = new AtomicInteger(0);
	public static AtomicInteger copyChallengeCount = new AtomicInteger(0);
	public static AtomicInteger copySuccessCount = new AtomicInteger(0);
	public static AtomicInteger copyFailCount = new AtomicInteger(0);
	public static AtomicInteger arenaTryCount = new AtomicInteger(0);
	public static AtomicInteger arenaChallengeCount = new AtomicInteger(0);
	public static AtomicInteger arenaSuccessCount = new AtomicInteger(0);
	public static AtomicInteger arenaFailCount = new AtomicInteger(0);
	public static AtomicInteger worldBossTryCount = new AtomicInteger(0);
	public static AtomicInteger worldBossChallengeCount = new AtomicInteger(0);
	public static AtomicInteger worldBossSuccessCount = new AtomicInteger(0);
	public static AtomicInteger worldBossFailCount = new AtomicInteger(0);
	public static AtomicInteger towerUpTryCount = new AtomicInteger(0);
	public static AtomicInteger towerUpChallengeCount = new AtomicInteger(0);
	public static AtomicInteger towerUpSuccessCount = new AtomicInteger(0);
	public static AtomicInteger towerUpFailCount = new AtomicInteger(0);
	public static AtomicInteger guardNPCTryCount = new AtomicInteger(0);
	public static AtomicInteger guardNPCChallengeCount = new AtomicInteger(0);
	public static AtomicInteger guardNPCSuccessCount = new AtomicInteger(0);
	public static AtomicInteger guardNPCFailCount = new AtomicInteger(0);
	public static AtomicInteger expeditionTryCount = new AtomicInteger(0);
	public static AtomicInteger expeditionChallengeCount = new AtomicInteger(0);
	public static AtomicInteger expeditionSuccessCount = new AtomicInteger(0);
	public static AtomicInteger expeditionFailCount = new AtomicInteger(0);
	public static AtomicInteger treasureRoadTryCount = new AtomicInteger(0);
	public static AtomicInteger treasureRoadChallengeCount = new AtomicInteger(0);
	public static AtomicInteger treasureRoadSuccessCount = new AtomicInteger(0);
	public static AtomicInteger treasureRoadFailCount = new AtomicInteger(0);
	public static AtomicInteger normalFinishCount = new AtomicInteger(0);
	public static AtomicInteger errorFinishCount = new AtomicInteger(0);
	public static AtomicInteger unexpectedFinishCount = new AtomicInteger(0);
	public static AtomicInteger connectionCloseCount = new AtomicInteger(0);

	private static Random rand = new Random();

	public static void main(String[] args)
	{
		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		int clientNumber = 50;

		for (int i = 0; i < clientNumber; ++i)
		{
			Pair<String, Integer> p = Pair.makePair(String.format("nmmo%04d", i), 1002 + rand.nextInt(3));
			InternalClientHandler.userQueue.add(p);
		}

		MultiClient host = new MultiClient();
		Bootstrap bootstrap = host.PrepareBootstrap(clientNumber);
		if (bootstrap == null)
			return;

		Future<?>[] fs = new Future<?>[clientNumber];
		for (int i = 0; i < fs.length; ++i)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (Exception ignored)
			{
			}
			fs[i] = host.StartClient(i, "127.0.0.1", 6868, bootstrap);
		}
		for (Future<?> f : fs)
		{
			try
			{
				f.sync();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		bootstrap.config().group().shutdownGracefully();
	}

	public ChannelFuture StartClient(int index, String host, int port, Bootstrap bootstrap)
	{
		SingleClient client = new SingleClient(host, port, index, bootstrap);
		System.out.println("Start client: " + index);
		return client.Start();
	}

	public Bootstrap PrepareBootstrap(int clientNumber)
	{
		EventLoopGroup workerGroup = new NioEventLoopGroup(4);
		try
		{
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(ExtendedNioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				public void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast(new InternalClientEncoder());
					ch.pipeline().addLast(new InternalClientDecoder());
					ch.pipeline().addLast(new InternalClientHandler());
				}
			});
			return b;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			workerGroup.shutdownGracefully();
		}
		return null;
	}

}
