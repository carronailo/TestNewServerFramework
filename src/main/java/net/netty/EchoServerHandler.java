package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter
{
	@Override
	public void channelActive(final ChannelHandlerContext ctx)
	{
		System.out.println("conn: online " + ctx.channel().remoteAddress().toString());
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx)
	{
		System.out.println("conn: offline " + ctx.channel().remoteAddress().toString());
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg)
	{
		System.out.println("recv: " + ((ByteBuf)msg).toString(CharsetUtil.UTF_8));
		final ChannelFuture f = ctx.writeAndFlush(msg);
		f.addListener((ChannelFutureListener) future ->
		{
			assert f == future;
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
