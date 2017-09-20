package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 16:43 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ClientSide.BATTLE_RequestPVP)
public class RequestPVPMsg
{
	public long opponentRoleID;
	public byte type;
}
