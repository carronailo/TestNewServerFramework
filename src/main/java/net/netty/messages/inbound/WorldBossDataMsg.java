package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/18 14:51 for TestNewServerFramework.
 */
@RPC(CID = CID.WORLDBOSS, MID = MID.ServerSide.WORLD_BOSS_WorldBossData)
public class WorldBossDataMsg
{
	public int bossCurHP;
	public int bossTotalHP;
	public short challengeLeftCount;
	public short challengeTotalCount;
	public int onceFightTime;
}
