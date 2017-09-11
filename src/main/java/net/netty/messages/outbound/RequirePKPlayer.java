package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 11:00 for TestNewServerFramework.
 */
@RPC(CID = CID.ARENA_SOLO, MID = MID.ClientSide.ARENA_SOLO_RequirePKPlayer)
public class RequirePKPlayer
{
	public long opponentRoleID;
}
