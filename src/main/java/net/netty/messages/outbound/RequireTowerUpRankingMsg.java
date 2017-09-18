package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.TOWERUP, MID = MID.ClientSide.TOWER_UP_RequireTowerUpRanking)
public class RequireTowerUpRankingMsg
{
}
