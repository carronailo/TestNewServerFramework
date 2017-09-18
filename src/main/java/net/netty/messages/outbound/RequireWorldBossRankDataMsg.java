package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/18 14:24 for TestNewServerFramework.
 */
@RPC(CID = CID.WORLDBOSS, MID = MID.ClientSide.WORLD_BOSS_RequireWorldBossRankData)
public class RequireWorldBossRankDataMsg
{
}
