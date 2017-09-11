package net.netty.messages.inbound;

import net.netty.messages.ArenaRecord;
import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 11:31 for TestNewServerFramework.
 */
@RPC(CID = CID.ARENA_SOLO, MID = MID.ServerSide.ARENA_SOLO_ArenaRankInfo)
public class ArenaRankInfoMsg
{
	public int highestRank;
	public short challengeChance;
	public short resetChance;
	public int coolDownRemain;
	public int rank;
	public ArenaRecord[] challengeTargets;
}
