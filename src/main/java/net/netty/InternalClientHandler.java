package net.netty;

import common.utility.Debug;
import common.utility.Pair;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
	private static final int COPYCHALLENGECOUNT = 1;

	private static final boolean TEST_LOGIN = true;
	private static final boolean TEST_PVE_COPY = true;
	private static final boolean TEST_PVP_ARENA = true;

	private static final boolean STOP_ON_FINISH = true;

	private int arenaChallengeTimes = 0;
	private int copyChallengeTimes = 0;

	private boolean closeByMe = false;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		if (!userQueue.isEmpty())
		{
			Pair<String, Integer> p = userQueue.poll();
			if (p != null)
			{
				((ExtendedNioSocketChannel) ctx.channel()).username = p.first;
				((ExtendedNioSocketChannel) ctx.channel()).templateID = p.second;
				((ExtendedNioSocketChannel) ctx.channel()).serverID = SERVERID;
			}
			else
			{
				closeByMe = true;
				ctx.close();
				MultiClient.errorFinishCount.addAndGet(1);
			}
		}
		else
		{
			closeByMe = true;
			ctx.close();
			MultiClient.errorFinishCount.addAndGet(1);
		}
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		Pair<String, Integer> p = Pair.makePair(((ExtendedNioSocketChannel) ctx.channel()).username, ((ExtendedNioSocketChannel) ctx.channel()).templateID);
		userQueue.add(p);
		System.out.println(String.format("[%s]断开连接", ((ExtendedNioSocketChannel) ctx.channel()).username));
		if(!closeByMe)
			MultiClient.unexpectedFinishCount.addAndGet(1);
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
		closeByMe = true;
		ctx.close();
		MultiClient.errorFinishCount.addAndGet(1);
	}

	private void HandleMessage(final ChannelHandlerContext ctx, Object msg) throws Exception
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
				HandleEnterScene(ctx, (SomeoneEnterSceneMsg) msg);
				break;
			case "ArenaRankListMsg":
				HandleArenaRankList(ctx, (ArenaRankListMsg) msg);
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

	private void HandleEcho(final ChannelHandlerContext ctx, EchoMsg msg) throws Exception
	{
		EchoReturnMsg newMsg = new EchoReturnMsg();
		newMsg.index = msg.Index;
		newMsg.time = System.currentTimeMillis();
		newMsg.key = "";
		ctx.writeAndFlush(newMsg);
	}

	private void HandleSecurity(final ChannelHandlerContext ctx) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		int serverID = ((ExtendedNioSocketChannel) ctx.channel()).serverID;
		if (TEST_LOGIN)
		{
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
				if(username != null && !username.isEmpty())
				{
					LoginMsg newMsg = new LoginMsg();
					newMsg.userName = username;
					newMsg.password = "111111";
					newMsg.isAdult = 1;
					newMsg.serverID = serverID;
					newMsg.deviceIdentifier = "";
					newMsg.deviceModel = "";
					ctx.writeAndFlush(newMsg);
					MultiClient.loginTryCount.addAndGet(1);
				}
				else
				{
					System.out.println("通道异常，username为空");
					closeByMe = true;
					ctx.close();
					MultiClient.loginFailCount.addAndGet(1);
					MultiClient.errorFinishCount.addAndGet(1);
				}
			}
		}
	}


	private void HandleLoginReturn(final ChannelHandlerContext ctx, LoginReturnMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		if (msg.returnValue == 1)
		{
			System.out.println(String.format("[%s]登录成功", username));
			MultiClient.loginSuccessCount.addAndGet(1);
		}
		else if (msg.returnValue == -4)
		{
			System.out.println(String.format("[%s]账号不存在", username));
			closeByMe = true;
			ctx.close();
			MultiClient.loginFailCount.addAndGet(1);
			MultiClient.errorFinishCount.addAndGet(1);
		}
		else if (msg.returnValue == -6)
		{
			System.out.println(String.format("[%s]账号已被注册", username));
			closeByMe = true;
			ctx.close();
			MultiClient.loginFailCount.addAndGet(1);
			MultiClient.errorFinishCount.addAndGet(1);
		}
		else
		{
			System.out.println(String.format("[%s]登录失败：[%d]", username, msg.returnValue));
			closeByMe = true;
			ctx.close();
			MultiClient.loginFailCount.addAndGet(1);
			MultiClient.errorFinishCount.addAndGet(1);
		}
	}

	private void HandleNoRole(final ChannelHandlerContext ctx, NoRoleMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		int templateID = ((ExtendedNioSocketChannel) ctx.channel()).templateID;
		System.out.println(String.format("[%s]创建角色[%d]", username, templateID));
		CreateRoleMsg newMsg = new CreateRoleMsg();
		newMsg.roleName = username;
		newMsg.templateID = templateID;
		ctx.writeAndFlush(newMsg);
	}

	private void HandleEnterWorld(final ChannelHandlerContext ctx, EnterWorldMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
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
		if (TEST_PVE_COPY)
		{
			// 登录测试完毕，开始测试副本
			RequestPVEMsg newMsg = new RequestPVEMsg();
			newMsg.copyID = 10001;
			newMsg.pveType = 101;
			System.out.println(String.format("[%s]挑战副本[%d]", username, newMsg.copyID));
			ctx.write(newMsg);
			MultiClient.copyTryCount.addAndGet(1);
		}
		else if (TEST_PVP_ARENA)
		{
			// 登录测试完毕，开始测试竞技场
			RequireArenaRankListMsg newMsg = new RequireArenaRankListMsg();
			ctx.write(newMsg);
		}
		else if (STOP_ON_FINISH)
		{
			// 啥都不测了，直接断开
			ctx.flush();
			closeByMe = true;
			ctx.close();
			MultiClient.normalFinishCount.addAndGet(1);
			return;
		}
		ctx.flush();
	}

	private void HandleEnterScene(final ChannelHandlerContext ctx, SomeoneEnterSceneMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
//		System.out.println(String.format("[%s]: [%s]进入场景", username, msg.nick));
	}

	private void HandleArenaRankList(final ChannelHandlerContext ctx, ArenaRankListMsg msg) throws Exception
	{
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		RequireUpdateArenaInfo newMsg = new RequireUpdateArenaInfo();
		ctx.writeAndFlush(newMsg);
	}

	private void HandleArenaRankInfo(final ChannelHandlerContext ctx, ArenaRankInfoMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		System.out.println(String.format("[%s]竞技场排名[%d]", username, msg.rank));
		if (arenaChallengeTimes < ARENACHALLENGECOUNT)
		{
			RequirePKPlayer newMsg = new RequirePKPlayer();
			newMsg.opponentRoleID = msg.challengeTargets[2].roleID;
			System.out.println(String.format("[%s]挑战[%s][%d]", username, msg.challengeTargets[2].nick, msg.challengeTargets[2].roleID));
			ctx.writeAndFlush(newMsg);
			MultiClient.arenaTryCount.addAndGet(1);
		}
	}

	private void HandleRequestPVEReturn(final ChannelHandlerContext ctx, RequestPVEReturnMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		++copyChallengeTimes;
		if (msg.result == 0)
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
			MultiClient.copyChallengeCount.addAndGet(1);
		}
		else
		{
			System.err.println(String.format("[%s]请求挑战副本失败：[%d]", username, msg.result));
			MultiClient.copyFailCount.addAndGet(1);
			if (copyChallengeTimes >= COPYCHALLENGECOUNT)
			{
				if (TEST_PVP_ARENA)
				{
					// 副本挑战测试结束，转向竞技场挑战测试
					RequireArenaRankListMsg newMsg = new RequireArenaRankListMsg();
					ctx.write(newMsg);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					closeByMe = true;
					ctx.close();
					MultiClient.normalFinishCount.addAndGet(1);
					return;
				}
			}
			else
			{
				RequestPVEMsg newMsg = new RequestPVEMsg();
				newMsg.copyID = 10001;
				newMsg.pveType = 101;
				System.out.println(String.format("[%s]挑战副本[%d]", username, newMsg.copyID));
				ctx.write(newMsg);
				MultiClient.copyTryCount.addAndGet(1);
			}
			ctx.flush();
		}
	}

	private void HandleRequestPVPReturn(final ChannelHandlerContext ctx, RequestPVPReturnMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		++arenaChallengeTimes;
		if (msg.result == 0)
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
			MultiClient.arenaChallengeCount.addAndGet(1);
		}
		else
		{
			System.err.println(String.format("[%s]请求挑战竞技场失败：[%d]", username, msg.result));
			MultiClient.arenaFailCount.addAndGet(1);
			if (arenaChallengeTimes >= ARENACHALLENGECOUNT)
			{
				// 竞技场挑战测试结束，转向其他挑战
				if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					closeByMe = true;
					ctx.close();
					MultiClient.normalFinishCount.addAndGet(1);
					return;
				}
			}
		}
	}

	private void HandlePVEReckoningInfo(final ChannelHandlerContext ctx, PVEReckoningInfoMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		if (msg.result == 1)
		{
			MultiClient.copySuccessCount.addAndGet(1);
			System.out.println(String.format("[%s]副本战斗胜利", username));
		}
		else
		{
			MultiClient.copyFailCount.addAndGet(1);
			System.err.println(String.format("[%s]副本战斗失败", username));
		}
		if (copyChallengeTimes >= COPYCHALLENGECOUNT)
		{
			if (TEST_PVP_ARENA)
			{
				// 副本挑战测试结束，转向竞技场挑战测试
				RequireArenaRankListMsg newMsg = new RequireArenaRankListMsg();
				ctx.write(newMsg);
			}
			else if (STOP_ON_FINISH)
			{
				// 啥都不测了，直接断开
				closeByMe = true;
				ctx.close();
				MultiClient.normalFinishCount.addAndGet(1);
				return;
			}
		}
		else
		{
			RequestPVEMsg newMsg = new RequestPVEMsg();
			newMsg.copyID = 10001;
			newMsg.pveType = 101;
			System.out.println(String.format("[%s]挑战副本[%d]", username, newMsg.copyID));
			ctx.write(newMsg);
			MultiClient.copyTryCount.addAndGet(1);
		}
		ctx.flush();
	}

	private void HandlePVPReckoningInfo(final ChannelHandlerContext ctx, PVPReckoningInfoMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		if (msg.result == 1)
		{
			MultiClient.arenaSuccessCount.addAndGet(1);
			System.out.println(String.format("[%s]竞技场战斗胜利，排名[%d]>>[%d]", username, msg.myOldRank, msg.myNewRank));
		}
		else
		{
			MultiClient.arenaFailCount.addAndGet(1);
			System.err.println(String.format("[%s]竞技场战斗失败", username));
		}
		if (arenaChallengeTimes >= ARENACHALLENGECOUNT)
		{
			// 竞技场挑战测试结束，转向其他挑战
			if (STOP_ON_FINISH)
			{
				// 啥都不测了，直接断开
				closeByMe = true;
				ctx.close();
				MultiClient.normalFinishCount.addAndGet(1);
				return;
			}
		}
	}
}
