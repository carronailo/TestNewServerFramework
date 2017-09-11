package net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import javafx.util.Pair;
import net.netty.messages.InBoundMessageMap;
import net.netty.messages.OutBoundMessageMap;

import java.util.Random;

/**
 * Created by CarroNailo on 2017/9/6 12:27 for TestNewServerFramework.
 */
public class SingleClient
{
	public static void main(String[] args)
	{
		InBoundMessageMap.getInstance();
		OutBoundMessageMap.getInstance();

		EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		InternalClientHandler.userQueue.add(new Pair<>("nmmo0000", 1002));
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
			SingleClient client = new SingleClient("127.0.0.1", 6868, 0, b);
			System.out.println("Start client: " + 0);
			client.Start().sync();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			workerGroup.shutdownGracefully();
		}
	}

	private Bootstrap bootstrap;
	private String host = "127.0.0.1";
	private int port = 8080;
	private int clientIndex = 0;

	public SingleClient(String host, int port, int clientIndex, Bootstrap bootstrap)
	{
		this.host = host;
		this.port = port;
		this.clientIndex = clientIndex;
		this.bootstrap = bootstrap;
	}

	public ChannelFuture Start()
	{
		try
		{
			// Start the client.
			ChannelFuture f = bootstrap.connect(host, port).sync();

			// Wait until the connection is closed.
//			f.channel().closeFuture().sync();

			return f.channel().closeFuture();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{

		}
		return null;
	}

}
