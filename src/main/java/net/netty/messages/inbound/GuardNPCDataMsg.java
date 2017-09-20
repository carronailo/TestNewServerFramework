package net.netty.messages.inbound;

import net.netty.messages.BoxAwardInfo;
import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 14:56 for TestNewServerFramework.
 */
@RPC(CID = CID.GUARDNPC, MID = MID.ServerSide.GUARDNPC_GuardNPCData)
public class GuardNPCDataMsg
{
	public byte challengeCount;
	public byte maxChallengeLevel;
	public short challengeWave;
	public BoxAwardInfo[] boxAwardInfo;
}
