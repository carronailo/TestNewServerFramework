package net.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.util.Pair;
import net.netty.messages.inbound.*;
import net.netty.messages.outbound.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by CarroNailo on 2017/9/6 18:28 for TestNewServerFramework.
 */
public class InternalClientHandler extends ChannelInboundHandlerAdapter
{
	public static ConcurrentLinkedQueue<Pair<String, Integer>> userQueue = new ConcurrentLinkedQueue<>();

	private static final boolean REGISTER = false;

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
			case "SomeoneEnterSceneMsg":
				HandlerEnterScene(ctx, (SomeoneEnterSceneMsg) msg);
				break;
			default:
//				System.out.println("未处理的消息");
				break;
		}
	}

	void HandleEcho(final ChannelHandlerContext ctx, EchoMsg msg)
	{
		EchoReturnMsg newMsg = new EchoReturnMsg();
		newMsg.index = msg.Index;
		newMsg.time = System.currentTimeMillis();
		newMsg.key = "";
		ctx.writeAndFlush(newMsg);
	}

	void HandleSecurity(final ChannelHandlerContext ctx)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			if (REGISTER)
			{
				RegisterMsg newMsg = new RegisterMsg();
				newMsg.userName = username;
				newMsg.password = "111111";
				newMsg.isAdult = 1;
				newMsg.serverID = 18;
				newMsg.deviceIdentifier = "";
				newMsg.deviceModel = "";
				ctx.writeAndFlush(newMsg);
			}
			else
			{
				LoginMsg newMsg = new LoginMsg();
				newMsg.userName = username;
				newMsg.password = "111111";
				newMsg.isAdult = 1;
				newMsg.serverID = 18;
				newMsg.deviceIdentifier = "";
				newMsg.deviceModel = "";
				ctx.writeAndFlush(newMsg);
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
			CreateRoleMsg newMsg = new CreateRoleMsg();
			newMsg.roleName = username;
			newMsg.templateID = templateID;
			ctx.writeAndFlush(newMsg);
		}
		else
			ctx.close();
	}

	void HandleEnterWorld(final ChannelHandlerContext ctx, EnterWorldMsg msg)
	{
		System.out.println(String.format("[%d][%d][%s]进入游戏", msg.roleID, msg.roleTemplateID, msg.nickName));
		ctx.write(new RequestDetailMsg());
		EnterSceneMsg newMsg = new EnterSceneMsg();
		newMsg.sceneID = 1001;
		newMsg.x = 0;
		newMsg.y = 0;
		newMsg.z = 0;
		ctx.write(newMsg);
		ctx.flush();
	}

	void HandlerEnterScene(final ChannelHandlerContext ctx, SomeoneEnterSceneMsg msg)
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
}
