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
import java.util.concurrent.TimeUnit;

/**
 * Created by CarroNailo on 2017/9/6 10:31 for TestNewServerFramework.
 */
public class MultiClient
{
	public static volatile int loginSuccessCount = 0;
	public static volatile int loginFailCount = 0;
	public static volatile int copyChallengeCount = 0;
	public static volatile int copySuccessCount = 0;
	public static volatile int copyFailCount = 0;
	public static volatile int arenaChallengeCount = 0;
	public static volatile int arenaSuccessCount = 0;
	public static volatile int arenaFailCount = 0;
	public static volatile int normalFinishCount = 0;
	public static volatile int errorFinishCount = 0;

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
		EventLoopGroup workerGroup = new NioEventLoopGroup(32);
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
