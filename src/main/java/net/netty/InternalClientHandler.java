package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.util.Pair;
import net.netty.messages.*;

import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by CarroNailo on 2017/9/6 18:28 for TestNewServerFramework.
 */
public class InternalClientHandler extends ChannelInboundHandlerAdapter
{
	public static ConcurrentLinkedQueue<Pair<String, Integer>> userQueue = new ConcurrentLinkedQueue<>();

	private static final boolean REGISTER = false;

//	private String username;
//	private String password = "111111";
//	private int templateID;
//
//	public InternalClientHandler(int index, String username, int templateID)
//	{
//		this.username = username;
//		this.templateID = templateID;
//	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			Pair<String, Integer> p = userQueue.poll();
			if (p != null)
			{
				((ExtendedNioSocketChannel) channel).username = p.getKey();
				((ExtendedNioSocketChannel) channel).templateID = p.getValue();
			}
			else
				ctx.close();
		}
		else
			ctx.close();
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
			System.out.println(String.format("[%s]断开连接", ((ExtendedNioSocketChannel) channel).username));
	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		if (msg != null)
		{
			HandleMessage(ctx, msg);
		}
//		else
//			System.out.println("Discard unhandled msg");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
		ctx.close();
	}

	void HandleMessage(final ChannelHandlerContext ctx, Object msg)
	{
		switch (msg.getClass().getSimpleName())
		{
			case "LoginReturnMsg":
				HandleLoginReturn(ctx, (LoginReturnMsg) msg);
				break;
			case "NoRoleMsg":
				HandleNoRole(ctx, (NoRoleMsg) msg);
				break;
			case "EnterWorldMsg":
				HandleEnterWorld(ctx, (EnterWorldMsg) msg);
				break;
			case "SecurityMsg":
				HandleSecurity(ctx);
				break;
			case "SHA1Msg":
				break;
			case "EchoMsg":
				HandleEcho(ctx, (EchoMsg) msg);
				break;
			case "EnterSceneMsg":
				HandlerEnterScene(ctx, (EnterSceneMsg) msg);
				break;
			default:
//				System.out.println("未处理的消息");
				break;
		}
	}

	void HandleEcho(final ChannelHandlerContext ctx, EchoMsg msg)
	{
		ByteBuf buf = Unpooled.buffer();
		PackEchoReturnMsg(msg.Index, System.currentTimeMillis(), buf);
		ctx.writeAndFlush(buf);
	}

	void HandleSecurity(final ChannelHandlerContext ctx)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			if (REGISTER)
			{
				ByteBuf buf = Unpooled.buffer();
				PackRegisterMsg(username, buf);
				ctx.writeAndFlush(buf);
			}
			else
			{
				ByteBuf buf = Unpooled.buffer();
				PackLoginMsg(username, buf);
				ctx.writeAndFlush(buf);
			}
		}
		else
			ctx.close();
	}


	void HandleLoginReturn(final ChannelHandlerContext ctx, LoginReturnMsg msg)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			if (msg.returnValue == 1)
			{
				System.out.println(String.format("[%s]登录成功", username));
			}
			else if (msg.returnValue == -4)
			{
				System.out.println(String.format("[%s]账号不存在", username));
				ctx.close();
			}
			else if (msg.returnValue == -6)
			{
				System.out.println(String.format("[%s]账号已被注册", username));
				ctx.close();
			}
			else
			{
				System.out.println(String.format("[%s]登录失败：[%d]", username, msg.returnValue));
				ctx.close();
			}
		}
		else
			ctx.close();
	}

	void HandleNoRole(final ChannelHandlerContext ctx, NoRoleMsg msg)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			int templateID = ((ExtendedNioSocketChannel) channel).templateID;
			System.out.println(String.format("[%s]创建角色[%d]", username, templateID));
			ByteBuf buf = Unpooled.buffer();
			PackCreateRoleMsg(username, buf);
			ctx.writeAndFlush(buf);
		}
		else
			ctx.close();
	}

	void HandleEnterWorld(final ChannelHandlerContext ctx, EnterWorldMsg msg)
	{
		System.out.println(String.format("[%d][%d][%s]进入游戏", msg.roleID, msg.roleTemplateID, msg.nickName));
		ByteBuf buf = Unpooled.buffer();
		PackRequestDetailMsg(buf);
		ctx.write(buf);
		buf = Unpooled.buffer();
		PackEnterCityMsg(buf);
		ctx.write(buf);
		ctx.flush();
	}

	void HandlerEnterScene(final ChannelHandlerContext ctx, EnterSceneMsg msg)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
//			System.out.println(String.format("[%s]: [%s]进入场景", username, msg.nick));
		}
		else
			ctx.close();
	}

	void PackEchoReturnMsg(int index, long time, ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(37);
		buffer.writeByte(1);
		buffer.writeInt(index);
		buffer.writeLong(time);
		buffer.writeShort(0);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackRegisterMsg(String username, ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(2);
		byte[] usernameBytes = username.getBytes(Charset.forName("UTF8"));
		buffer.writeShort(usernameBytes.length);
		buffer.writeBytes(usernameBytes);
		byte[] passwordBytes = "111111".getBytes(Charset.forName("UTF8"));
		buffer.writeShort(passwordBytes.length);
		buffer.writeBytes(passwordBytes);
		buffer.writeInt(1);
		buffer.writeInt(18);
		buffer.writeShort(0);
		buffer.writeShort(0);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackLoginMsg(String username, ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		byte[] usernameBytes = username.getBytes(Charset.forName("UTF8"));
		buffer.writeShort(usernameBytes.length);
		buffer.writeBytes(usernameBytes);
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

	void PackCreateRoleMsg(String username, ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(1);
		byte[] usernameBytes = username.getBytes(Charset.forName("UTF8"));
		buffer.writeShort(usernameBytes.length);
		buffer.writeBytes(usernameBytes);
		buffer.writeInt(1004);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackEnterCityMsg(ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(1);
		buffer.writeByte(0);
		buffer.writeShort(1001);
		buffer.writeShort(0);
		buffer.writeShort(0);
		buffer.writeShort(0);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}

	void PackRequestDetailMsg(ByteBuf buffer)
	{
		buffer.writeByte(0);
		buffer.writeByte(0);
		buffer.writeByte(8);
		buffer.writeByte(10);
		int len = buffer.readableBytes();
		buffer.writerIndex(0);
		buffer.writeShort(len - 2);
		buffer.writerIndex(len);
	}
}
