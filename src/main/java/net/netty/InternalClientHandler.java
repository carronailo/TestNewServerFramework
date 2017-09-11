package net.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.util.Pair;
import net.netty.messages.inbound.*;
import net.netty.messages.outbound.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by CarroNailo on 2017/9/6 18:28 for TestNewServerFramework.
 */
public class InternalClientHandler extends ChannelInboundHandlerAdapter
{
	public static ConcurrentLinkedQueue<Pair<String, Integer>> userQueue = new ConcurrentLinkedQueue<>();
	public static List<Long> roleList = new ArrayList<>();

	private static final boolean REGISTER = false;
	private static final int SERVERID = 11;
	private static final int ARENACHALLENGECOUNT = 1;

	private int arenaChallengeTimes = 0;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			if(!userQueue.isEmpty())
			{
				Pair<String, Integer> p = userQueue.poll();
				if (p != null)
				{
					((ExtendedNioSocketChannel) channel).username = p.getKey();
					((ExtendedNioSocketChannel) channel).templateID = p.getValue();
					((ExtendedNioSocketChannel) channel).serverID = SERVERID;
				}
				else
				{
					ctx.close();
					MultiClient.errorFinishCount++;
				}
			}
			else
			{
				ctx.close();
				MultiClient.errorFinishCount++;
			}
		}
		else
		{
			ctx.close();
			MultiClient.errorFinishCount++;
		}
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			Pair<String, Integer> p = new Pair<>(((ExtendedNioSocketChannel) channel).username, ((ExtendedNioSocketChannel) channel).templateID);
			userQueue.add(p);
			System.out.println(String.format("[%s]断开连接", ((ExtendedNioSocketChannel) channel).username));
		}
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
		MultiClient.errorFinishCount++;
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
			case "ArenaRankInfoMsg":
				HandleArenaRankInfo(ctx, (ArenaRankInfoMsg) msg);
				break;
			case "RequestPVPReturnMsg":
				HandleRequestPVPReturn(ctx, (RequestPVPReturnMsg) msg);
				break;
			case "PVPReckoningInfoMsg":
				HandlePVPReckoningInfo(ctx, (PVPReckoningInfoMsg) msg);
				break;
			case "RequestPVEReturnMsg":
				HandleRequestPVEReturn(ctx, (RequestPVEReturnMsg) msg);
				break;
			case "PVEReckoningInfoMsg":
				HandlePVEReckoningInfo(ctx, (PVEReckoningInfoMsg) msg);
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
			int serverID = ((ExtendedNioSocketChannel) channel).serverID;
			if (REGISTER)
			{
				RegisterMsg newMsg = new RegisterMsg();
				newMsg.userName = username;
				newMsg.password = "111111";
				newMsg.isAdult = 1;
				newMsg.serverID = serverID;
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
				newMsg.serverID = serverID;
				newMsg.deviceIdentifier = "";
				newMsg.deviceModel = "";
				ctx.writeAndFlush(newMsg);
			}
		}
		else
		{
			ctx.close();
			MultiClient.errorFinishCount++;
		}
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
				MultiClient.loginSuccessCount++;
			}
			else if (msg.returnValue == -4)
			{
				System.err.println(String.format("[%s]账号不存在", username));
				ctx.close();
				MultiClient.errorFinishCount++;
			}
			else if (msg.returnValue == -6)
			{
				System.err.println(String.format("[%s]账号已被注册", username));
				ctx.close();
				MultiClient.errorFinishCount++;
			}
			else
			{
				System.err.println(String.format("[%s]登录失败：[%d]", username, msg.returnValue));
				ctx.close();
				MultiClient.errorFinishCount++;
			}
		}
		else
		{
			ctx.close();
			MultiClient.errorFinishCount++;
		}
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
		{
			ctx.close();
			MultiClient.errorFinishCount++;
		}
	}

	void HandleEnterWorld(final ChannelHandlerContext ctx, EnterWorldMsg msg)
	{
		System.out.println(String.format("[%d][%d][%s]进入游戏", msg.roleID, msg.roleTemplateID, msg.nickName));
		ctx.write(new RequestDetailMsg());
		{
			EnterSceneMsg newMsg = new EnterSceneMsg();
			newMsg.sceneID = 1001;
			newMsg.x = 0;
			newMsg.y = 0;
			newMsg.z = 0;
			ctx.write(newMsg);
		}
		{
			RequireUpdateArenaInfo newMsg = new RequireUpdateArenaInfo();
			ctx.write(newMsg);
		}
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
		{
			ctx.close();
			MultiClient.errorFinishCount++;
		}
	}

	void HandleArenaRankInfo(final ChannelHandlerContext ctx, ArenaRankInfoMsg msg)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			System.out.println(String.format("[%s]竞技场排名[%d]", username, msg.rank));
			if (arenaChallengeTimes < ARENACHALLENGECOUNT)
			{
				RequirePKPlayer newMsg = new RequirePKPlayer();
				newMsg.opponentRoleID = msg.challengeTargets[2].roleID;
				System.out.println(String.format("[%s]挑战[%s][%d]", username, msg.challengeTargets[2].nick, msg.challengeTargets[2].roleID));
				ctx.writeAndFlush(newMsg);
			}
		}
	}

	void HandleRequestPVEReturn(final ChannelHandlerContext ctx, RequestPVEReturnMsg msg)
	{

	}

	void HandleRequestPVPReturn(final ChannelHandlerContext ctx, RequestPVPReturnMsg msg)
	{
		{
			SubmitStartBattleMsg newMsg = new SubmitStartBattleMsg();
			ctx.write(newMsg);
		}
		{
			RequestReckoningMsg newMsg = new RequestReckoningMsg();
			newMsg.battleTime = 0;
			newMsg.hpRemain = msg.charAttributes[3];
			newMsg.playerAttributes = msg.charAttributes;
			newMsg.turnAttributes = new int[0];
			newMsg.result = 1;
			ctx.write(newMsg);
		}
		ctx.flush();
	}

	void HandlePVEReckoningInfo(final ChannelHandlerContext ctx, PVEReckoningInfoMsg msg)
	{

	}

	void HandlePVPReckoningInfo(final ChannelHandlerContext ctx, PVPReckoningInfoMsg msg)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			if (msg.result == 1)
				System.out.println(String.format("[%s]竞技场战斗胜利，排名[%d]>>[%d]", username, msg.myOldRank, msg.myNewRank));
			else
				System.err.println(String.format("[%s]竞技场战斗失败", username));
			++arenaChallengeTimes;
		}
		if (arenaChallengeTimes >= ARENACHALLENGECOUNT)
		{
			ctx.close();
			MultiClient.normalFinishCount++;
		}
	}
}
