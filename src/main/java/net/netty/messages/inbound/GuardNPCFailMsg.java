package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 15:33 for TestNewServerFramework.
 */
@RPC(CID = CID.GUARDNPC, MID = MID.ServerSide.GUARDNPC_GuardNPCFail)
public class GuardNPCFailMsg
{
	public byte failID;
}
