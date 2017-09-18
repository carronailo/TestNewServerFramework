package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.TowerUpRank;

@RPC(CID = CID.TOWERUP, MID = MID.ServerSide.TOWER_UP_TowerUpRanking)
public class TowerUpRankingMsg
{
	public TowerUpRank[] randkingData;//排行榜
}
