package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 11:11 for TestNewServerFramework.
 */
@RPC(CID = CID.ARENA_SOLO, MID = MID.ClientSide.ARENA_SOLO_RequireUpdateArenaInfo)
public class RequireUpdateArenaInfo
{
}
