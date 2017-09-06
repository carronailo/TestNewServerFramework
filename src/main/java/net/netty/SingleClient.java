package net.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by CarroNailo on 2017/9/6 12:27 for TestNewServerFramework.
 */
public class SingleClient
{
	private int port = 8080;
	private int clientIndex = 0;

	public SingleClient(int port, int clientIndex)
	{
		this.port = port;
		this.clientIndex = clientIndex;
	}

	public void Start()
	{
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>()
			{
				@Override
				public void initChannel(SocketChannel ch) throws Exception
				{
					ch.pipeline().addLast(new InternalClientDecoder());
					ch.pipeline().addLast(new InternalClientHandler(clientIndex));
				}
			});

			// Start the client.
			ChannelFuture f = b.connect("127.0.0.1", port).sync(); // (5)

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			workerGroup.shutdownGracefully();
		}

	}

}
