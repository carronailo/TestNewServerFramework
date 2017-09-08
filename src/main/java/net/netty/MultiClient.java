package net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.*;
import javafx.util.Pair;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by CarroNailo on 2017/9/6 10:31 for TestNewServerFramework.
 */
public class MultiClient
{
	public static void main(String[] args)
	{
		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		int clientNumber = 50;

		MultiClient host = new MultiClient();
		Bootstrap bootstrap = host.PrepareBootstrap(clientNumber);
		if(bootstrap == null)
			return;

		Future<?>[] fs = new Future<?>[clientNumber];
		for (int i = 0; i < fs.length; ++i)
		{
			final int index = i;
			try
			{
				Thread.sleep(100);
			}
			catch(Exception ex)
			{

			}
			fs[index] = host.StartClient(index, bootstrap);
		}
		for (; ; )
		{
			for (Future<?> f : fs)
			{
				try
				{
					f.sync();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	private int[] templateIDPool = new int[]{1002, 1003, 1004};
	private Random rand = new Random();

	ChannelFuture StartClient(int index, Bootstrap bootstrap)
	{
		SingleClient client = new SingleClient(6868, index, bootstrap);
		System.out.println("Start client: " + index);
		return client.Start();
	}

	Bootstrap PrepareBootstrap(int clientNumber)
	{
		EventLoopGroup workerGroup = new NioEventLoopGroup(8);
		for(int i = 0 ; i < clientNumber; ++i)
		{
			Pair<String, Integer> p =
				new Pair<>(String.format("nmmo%04d", i), templateIDPool[rand.nextInt(templateIDPool.length)]);
			InternalClientHandler.userQueue.add(p);
		}
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
