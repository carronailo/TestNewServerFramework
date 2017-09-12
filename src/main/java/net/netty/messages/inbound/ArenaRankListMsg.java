package net.netty.messages.inbound;

import net.netty.messages.ArenaRecord;
import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/12 17:42 for TestNewServerFramework.
 */
@RPC(CID = CID.ARENA_SOLO, MID = MID.ServerSide.ARENA_SOLO_ArenaRankList)
public class ArenaRankListMsg
{
	public ArenaRecord[] rankList;
}
