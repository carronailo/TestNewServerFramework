package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.netty.messages.LoginReturnMsg;

import java.nio.charset.Charset;

/**
 * Created by CarroNailo on 2017/9/6 18:28 for TestNewServerFramework.
 */
public class InternalClientHandler extends ChannelInboundHandlerAdapter
{
	private final ByteBuf fireMessage;

	public InternalClientHandler(int index)
	{
		String msg = String.format("客户端[%d]消息", index);
		byte[] bytes = msg.getBytes(Charset.forName("UTF8"));
		fireMessage = Unpooled.buffer(bytes.length);
		fireMessage.writeBytes(bytes);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
//			ctx.writeAndFlush(fireMessage);
		ByteBuf buf = Unpooled.buffer();
		PackLoginMsg(buf);
		ctx.writeAndFlush(buf);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		if(msg != null)
		{
			HandleMessage(ctx, msg);
		}
//			ctx.writeAndFlush(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)
	{
//			ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}

	void HandleMessage(final ChannelHandlerContext ctx, Object msg)
	{
		System.out.println(msg.getClass());
		if(msg instanceof LoginReturnMsg)
		{
			HandleLoginReturn(ctx, (LoginReturnMsg)msg);
		}
	}

	void HandleLoginReturn(final ChannelHandlerContext ctx, LoginReturnMsg msg)
	{
		if(msg.returnValue == 1)
		{
			ByteBuf buf = Unpooled.buffer();
			PackCreateRoleMsg(buf);
			ctx.writeAndFlush(buf);
		}
		else
			System.out.println("登录失败：" + msg.returnValue);
	}


	void PackRegisterMsg(ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(2);
		byte[] username = "mmo5000".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(username.length);
		buffer.writeBytes(username);
		byte[] password = "111111".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(password.length);
		buffer.writeBytes(password);
		buffer.writeInt(1);
		buffer.writeInt(18);
		buffer.writeShort(0);
		buffer.writeShort(0);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackLoginMsg(ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		byte[] username = "mmo5000".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(username.length);
		buffer.writeBytes(username);
		byte[] password = "111111".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(password.length);
		buffer.writeBytes(password);
		buffer.writeInt(1);
		buffer.writeInt(18);
		buffer.writeShort(0);
		buffer.writeShort(0);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackCreateRoleMsg(ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(1);
		byte[] rolename = "mmo5000".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(rolename.length);
		buffer.writeBytes(rolename);
		buffer.writeInt(1004);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}
}
