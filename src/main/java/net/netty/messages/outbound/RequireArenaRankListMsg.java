package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/12 17:41 for TestNewServerFramework.
 */
@RPC(CID = CID.ARENA_SOLO, MID = MID.ClientSide.ARENA_SOLO_RequeireRankList)
public class RequireArenaRankListMsg
{
}
