package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.ReckoningAwardData;

/**
 * Created by CarroNailo on 2017/9/11 12:30 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ServerSide.BATTLE_PVEReckoningInfo)
public class PVEReckoningInfoMsg
{
	public byte type;
	public byte result;
	public int time;
	public int beHit;
	public short combo;
	public byte rank;
	public int score;
	public int highestScore;
	public ReckoningAwardData awardData;
}
