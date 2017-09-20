package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 14:55 for TestNewServerFramework.
 */
@RPC(CID = CID.GUARDNPC, MID = MID.ClientSide.GUARDNPC_RefreshWave)
public class RefreshGuardNPCWaveMsg
{
	public short updateWave;
}
