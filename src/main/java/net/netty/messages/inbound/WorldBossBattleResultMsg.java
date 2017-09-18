package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.ReckoningAwardData;

/**
 * Created by CarroNailo on 2017/9/18 14:54 for TestNewServerFramework.
 */
@RPC(CID = CID.WORLDBOSS, MID = MID.ServerSide.WORLD_BOSS_WorldBossBattleResult)
public class WorldBossBattleResultMsg
{
	public int bossState;//boss是不是死亡了
	public int myRank;//我的排名
	public int damageValue;//造成伤害
	public int awardID;//结算ID
	public ReckoningAwardData awardData;//奖励
}
