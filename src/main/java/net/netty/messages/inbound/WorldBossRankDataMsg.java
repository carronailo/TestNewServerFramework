package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.WorldBossRank;

/**
 * Created by CarroNailo on 2017/9/18 14:52 for TestNewServerFramework.
 */
@RPC(CID = CID.WORLDBOSS, MID = MID.ServerSide.WORLD_BOSS_WorldBossRankData)
public class WorldBossRankDataMsg
{
	public WorldBossRank[] rankingData;
}
