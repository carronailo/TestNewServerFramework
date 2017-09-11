package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.ReckoningAwardData;

/**
 * Created by CarroNailo on 2017/9/11 12:30 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ServerSide.BATTLE_PVPReckoningInfo)
public class PVPReckoningInfoMsg
{
	public byte type;
	public byte result;
	public ReckoningAwardData awardData;
	public int myOldRank;
	public int myNewRank;
	public int oppoOldRank;
	public int oppoNewRank;
}
