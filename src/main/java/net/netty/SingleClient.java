package net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;

/**
 * Created by CarroNailo on 2017/9/6 12:27 for TestNewServerFramework.
 */
public class SingleClient
{
	private Bootstrap bootstrap;
	private int port = 8080;
	private int clientIndex = 0;

	public SingleClient(int port, int clientIndex, Bootstrap bootstrap)
	{
		this.port = port;
		this.clientIndex = clientIndex;
		this.bootstrap = bootstrap;
	}

	public ChannelFuture Start()
	{
		try
		{
			// Start the client.
			ChannelFuture f = bootstrap.connect("127.0.0.1", port).sync();

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
