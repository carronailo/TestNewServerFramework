package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.RobTreasureRobRecord;

/**
 * Created by CarroNailo on 2017/9/19 16:33 for TestNewServerFramework.
 */
@RPC(CID = CID.TreasureRoad, MID = MID.ServerSide.TREASUREROAD_TRoadOpponent)
public class TRoadOpponentMsg
{
	public RobTreasureRobRecord[] challengeTargets;
}
