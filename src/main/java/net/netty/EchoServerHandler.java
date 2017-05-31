package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by CarroNailo on 2017/5/22.
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
	{
		System.out.println("recv: " + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
		final ChannelFuture f = ctx.writeAndFlush(msg);
		f.addListener(new ChannelFutureListener()
		{
			@Override
			public void operationComplete(ChannelFuture future)
			{
				assert f == future;
			}
		});
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}
}
