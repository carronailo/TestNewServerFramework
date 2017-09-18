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
	private static final int WORLDBOSSCHALLENGECOUNT = 1;
	private static final int TOWERUPCHALLENGECOUNT = 1;
	private static final int GUARDNPCCHALLENGECOUNT = 1;

	private static final boolean TEST_LOGIN = true;
	private static final boolean TEST_PVE_COPY = true;
	private static final boolean TEST_PVP_ARENA = true;
	private static final boolean TEST_WORLD_BOSS = true;
	private static final boolean TEST_TOWER_UP = true;
	private static final boolean TEST_GUARD_NPC = false;

	private static final boolean STOP_ON_FINISH = true;

	private int arenaChallengeTimes = 0;
	private int copyChallengeTimes = 0;
	private int worldBossChallengeTimes = 0;
	private int towerUpChallengeTimes = 0;
	private int guardNPCChallengeTimes = 0;

	private boolean closeByMe = false;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		if (!userQueue.isEmpty())
		{
			Pair<String, Integer> p = userQueue.poll();
//			Pair<String, Integer> p = (Pair<String, Integer>)userQueue.toArray()[userQueue.size() - 1];
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
			case "WorldBossDataMsg":
				HandleWorldBossData(ctx, (WorldBossDataMsg)msg);
				break;
			case "WorldBossRankDataMsg":
				HandleWorldBossRankData(ctx, (WorldBossRankDataMsg)msg);
				break;
			case "WorldBossBattleResultMsg":
				HandleWorldBossBattleResult(ctx, (WorldBossBattleResultMsg)msg);
				break;
			case "TowerUpDataMsg":
				HandleTowerUpData(ctx, (TowerUpDataMsg)msg);
				break;
			case "TowerUpRankingMsg":
				HandleTowerUpRankingMsg(ctx, (TowerUpRankingMsg)msg);
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
		ctx.flush();

		NextTest(ETestStep.Login, ctx, username);
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
		RequireUpdateArenaInfoMsg newMsg = new RequireUpdateArenaInfoMsg();
		ctx.writeAndFlush(newMsg);
	}

	private void HandleArenaRankInfo(final ChannelHandlerContext ctx, ArenaRankInfoMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		System.out.println(String.format("[%s]竞技场排名[%d]", username, msg.rank));
		if (arenaChallengeTimes >= ARENACHALLENGECOUNT)
			NextTest(ETestStep.Arena, ctx, username);
		else
			_StartTestArenaByRankInfo(ctx, msg, username);
	}

	private void HandleRequestPVEReturn(final ChannelHandlerContext ctx, RequestPVEReturnMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		switch (msg.pveType)
		{
			case 101:
				++copyChallengeTimes;
				if (msg.result == 0)
				{
					ProceedCopy(ctx, msg);
					MultiClient.copyChallengeCount.addAndGet(1);
				}
				else
				{
					System.err.println(String.format("[%s]请求挑战副本失败：[%d]", username, msg.result));
					MultiClient.copyFailCount.addAndGet(1);
					if (copyChallengeTimes >= COPYCHALLENGECOUNT)
						NextTest(ETestStep.Copy, ctx, username);
					else
						StartTestCopy(ctx, username);
				}
				break;
			case (byte)218:
				++worldBossChallengeTimes;
				if(msg.result == 0)
				{
					ProceedWorldBoss(ctx, msg);
					MultiClient.worldBossChallengeCount.addAndGet(1);
				}
				else
				{
					System.err.println(String.format("[%s]请求挑战世界Boss失败：[%d]", username, msg.result));
					MultiClient.worldBossFailCount.addAndGet(1);
					if(worldBossChallengeTimes >= WORLDBOSSCHALLENGECOUNT)
						NextTest(ETestStep.WorldBoss, ctx, username);
					else
						StartTestWorldBoss(ctx, username);
				}
				break;
			case (byte)212:
				++towerUpChallengeTimes;
				if(msg.result == 0)
				{
					ProceedTowerUp(ctx, msg);
					MultiClient.towerUpChallengeCount.addAndGet(1);
				}
				else
				{
					System.err.println(String.format("[%s]请求挑战爬塔失败：[%d]", username, msg.result));
					MultiClient.towerUpFailCount.addAndGet(1);
					if(towerUpChallengeTimes >= TOWERUPCHALLENGECOUNT)
						NextTest(ETestStep.TowerUp, ctx, username);
					else
						StartTestTowerUp(ctx, username);
				}
				break;
		}
	}

	private void HandleRequestPVPReturn(final ChannelHandlerContext ctx, RequestPVPReturnMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		++arenaChallengeTimes;
		if (msg.result == 0)
		{
			ProceedArena(ctx, msg);
			MultiClient.arenaChallengeCount.addAndGet(1);
		}
		else
		{
			System.err.println(String.format("[%s]请求挑战竞技场失败：[%d]", username, msg.result));
			MultiClient.arenaFailCount.addAndGet(1);
			StartTestArena(ctx, username);
		}
	}

	private void HandlePVEReckoningInfo(final ChannelHandlerContext ctx, PVEReckoningInfoMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		switch (msg.type)
		{
			case 11:
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
					NextTest(ETestStep.Copy, ctx, username);
				else
					StartTestCopy(ctx, username);
				break;
			case 84:
				if (msg.result == 1)
				{
					MultiClient.towerUpSuccessCount.addAndGet(1);
					System.out.println(String.format("[%s]爬塔战斗胜利", username));
				}
				else
				{
					MultiClient.towerUpFailCount.addAndGet(1);
					System.err.println(String.format("[%s]爬塔战斗失败", username));
				}
				if (towerUpChallengeTimes >= TOWERUPCHALLENGECOUNT)
					NextTest(ETestStep.TowerUp, ctx, username);
				else
					StartTestTowerUp(ctx, username);
				break;
		}
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
	}

	private void HandleWorldBossData(ChannelHandlerContext ctx, WorldBossDataMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		System.out.println(String.format("[%s]世界Boss血量[%d / %d], 挑战次数[%d / %d]", username, msg.bossCurHP, msg.bossTotalHP, msg.challengeLeftCount, msg.challengeTotalCount));
	}

	private void HandleWorldBossRankData(ChannelHandlerContext ctx, WorldBossRankDataMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		if (worldBossChallengeTimes >= WORLDBOSSCHALLENGECOUNT)
			NextTest(ETestStep.WorldBoss, ctx, username);
		else
			_StartTestWorldBossByRankData(ctx, msg, username);
	}

	private void HandleWorldBossBattleResult(ChannelHandlerContext ctx, WorldBossBattleResultMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		MultiClient.worldBossSuccessCount.addAndGet(1);
		System.out.println(String.format("[%s]世界Boss战斗胜利，排名[%d]，伤害[%d]", username, msg.myRank, msg.damageValue));
		if (worldBossChallengeTimes >= WORLDBOSSCHALLENGECOUNT)
			NextTest(ETestStep.WorldBoss, ctx, username);
		else
			StartTestWorldBoss(ctx, username);
	}

	private void HandleTowerUpData(ChannelHandlerContext ctx, TowerUpDataMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		System.out.println(String.format("[%s]爬塔 当前层[%d]", username, msg.curFloor));
	}

	private void HandleTowerUpRankingMsg(ChannelHandlerContext ctx, TowerUpRankingMsg msg) throws Exception
	{
		Debug.Assert((ctx.channel() instanceof ExtendedNioSocketChannel), "Channel类型不是ExtendedNioSocketChannel");
		String username = ((ExtendedNioSocketChannel) ctx.channel()).username;
		if (towerUpChallengeTimes >= TOWERUPCHALLENGECOUNT)
			NextTest(ETestStep.TowerUp, ctx, username);
		else
			_StartTestTowerUpByRankData(ctx, msg, username);
	}

	private void StartTestCopy(ChannelHandlerContext ctx, String username)
	{
		RequestPVEMsg newMsg = new RequestPVEMsg();
		newMsg.copyID = 10001;
		newMsg.pveType = 101;
		System.out.println(String.format("[%s]挑战副本[%d]", username, newMsg.copyID));
		ctx.writeAndFlush(newMsg);
		MultiClient.copyTryCount.addAndGet(1);
	}

	private void ProceedCopy(ChannelHandlerContext ctx, RequestPVEReturnMsg msg)
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

	private void StartTestArena(ChannelHandlerContext ctx, String username)
	{
		RequireArenaRankListMsg newMsg = new RequireArenaRankListMsg();
		ctx.writeAndFlush(newMsg);
	}

	private void _StartTestArenaByRankInfo(ChannelHandlerContext ctx, ArenaRankInfoMsg msg, String username)
	{
		RequirePKPlayerMsg newMsg = new RequirePKPlayerMsg();
		newMsg.opponentRoleID = msg.challengeTargets[2].roleID;
		System.out.println(String.format("[%s]挑战[%s][%d]", username, msg.challengeTargets[2].nick, msg.challengeTargets[2].roleID));
		ctx.writeAndFlush(newMsg);
		MultiClient.arenaTryCount.addAndGet(1);
	}

	private void ProceedArena(ChannelHandlerContext ctx, RequestPVPReturnMsg msg)
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

	private void StartTestWorldBoss(ChannelHandlerContext ctx, String username)
	{
		{
			RequireWorldBossDataMsg newMsg = new RequireWorldBossDataMsg();
			ctx.write(newMsg);
		}
		{
			RequireWorldBossRankDataMsg newMsg = new RequireWorldBossRankDataMsg();
			ctx.write(newMsg);
		}
		ctx.flush();
	}

	private void _StartTestWorldBossByRankData(ChannelHandlerContext ctx, WorldBossRankDataMsg msg, String username)
	{
		BeginChallengeWorldBossMsg newMsg = new BeginChallengeWorldBossMsg();
		System.out.println(String.format("[%s]挑战世界BOSS", username));
		ctx.writeAndFlush(newMsg);
		MultiClient.worldBossTryCount.addAndGet(1);
	}

	private void ProceedWorldBoss(ChannelHandlerContext ctx, RequestPVEReturnMsg msg)
	{
		{
			SubmitStartBattleMsg newMsg = new SubmitStartBattleMsg();
			ctx.write(newMsg);
		}
		{
			WorldBossHurtPerHitMsg newMsg = new WorldBossHurtPerHitMsg();
			newMsg.hurtValue = 1;
			ctx.write(newMsg);
		}
		{
			WorldBossSubmitHurtValueMsg newMsg = new WorldBossSubmitHurtValueMsg();
			newMsg.battleTime = 0;
			newMsg.hpRemain = msg.charAttributes[3];
			newMsg.playerAttributes = msg.charAttributes;
			newMsg.turnAttributes = new int[0];
			newMsg.hurtValue = 1;
			ctx.write(newMsg);
		}
		ctx.flush();
	}

	private void StartTestTowerUp(ChannelHandlerContext ctx, String username)
	{
		RequireTowerUpRankingMsg newMsg = new RequireTowerUpRankingMsg();
		ctx.writeAndFlush(newMsg);
	}

	private void _StartTestTowerUpByRankData(ChannelHandlerContext ctx, TowerUpRankingMsg msg, String username)
	{
		RequestPVEMsg newMsg = new RequestPVEMsg();
		newMsg.copyID = 40001;
		newMsg.pveType = (byte)212;
		System.out.println(String.format("[%s]挑战爬塔[%d]", username, newMsg.copyID));
		ctx.writeAndFlush(newMsg);
		MultiClient.towerUpTryCount.addAndGet(1);
	}

	private void ProceedTowerUp(ChannelHandlerContext ctx, RequestPVEReturnMsg msg)
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

	private void StartTestGuardNPC(ChannelHandlerContext ctx, String username)
	{

	}

	private void NextTest(ETestStep currentStep, ChannelHandlerContext ctx, String username)
	{
		switch (currentStep)
		{
			case Login:
				if (TEST_PVE_COPY)
				{
					// 登录测试完毕，开始测试副本
					StartTestCopy(ctx, username);
				}
				else if (TEST_PVP_ARENA)
				{
					// 登录测试完毕，开始测试竞技场
					StartTestArena(ctx, username);
				}
				else if(TEST_WORLD_BOSS)
				{
					// 登录测试完毕，开始测试世界Boss
					StartTestWorldBoss(ctx, username);
				}
				else if(TEST_TOWER_UP)
				{
					// 登录测试完毕，开始测试爬塔
					StartTestTowerUp(ctx, username);
				}
				else if(TEST_GUARD_NPC)
				{
					// 登录测试完毕，开始测试守护洛羽
					StartTestGuardNPC(ctx, username);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			case Copy:
				if (TEST_PVP_ARENA)
				{
					// 挑战副本测试完毕，开始测试竞技场
					StartTestArena(ctx, username);
				}
				else if(TEST_WORLD_BOSS)
				{
					// 挑战副本测试完毕，开始测试世界Boss
					StartTestWorldBoss(ctx, username);
				}
				else if(TEST_TOWER_UP)
				{
					// 挑战副本测试完毕，开始测试爬塔
					StartTestTowerUp(ctx, username);
				}
				else if(TEST_GUARD_NPC)
				{
					// 挑战副本测试完毕，开始测试守护洛羽
					StartTestGuardNPC(ctx, username);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			case Arena:
				if(TEST_WORLD_BOSS)
				{
					// 挑战竞技场测试完毕，开始测试世界Boss
					StartTestWorldBoss(ctx, username);
				}
				else if(TEST_TOWER_UP)
				{
					// 挑战竞技场测试完毕，开始测试爬塔
					StartTestTowerUp(ctx, username);
				}
				else if(TEST_GUARD_NPC)
				{
					// 挑战竞技场测试完毕，开始测试守护洛羽
					StartTestGuardNPC(ctx, username);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			case WorldBoss:
				if(TEST_TOWER_UP)
				{
					// 挑战世界BOSS测试完毕，开始测试爬塔
					StartTestTowerUp(ctx, username);
				}
				else if(TEST_GUARD_NPC)
				{
					// 挑战世界BOSS测试完毕，开始测试守护洛羽
					StartTestGuardNPC(ctx, username);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			case TowerUp:
				if(TEST_GUARD_NPC)
				{
					// 挑战爬塔测试完毕，开始测试守护洛羽
					StartTestGuardNPC(ctx, username);
				}
				else if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			case GuardNPC:
				if (STOP_ON_FINISH)
				{
					// 啥都不测了，直接断开
					StopTest(ctx);
				}
				break;
			default:
				break;
		}
	}

	private void StopTest(ChannelHandlerContext ctx)
	{
		ctx.flush();
		closeByMe = true;
		ctx.close();
		MultiClient.normalFinishCount.addAndGet(1);
	}

	private enum ETestStep
	{
		Login,
		Copy,
		Arena,
		WorldBoss,
		TowerUp,
		GuardNPC,
	}
}
