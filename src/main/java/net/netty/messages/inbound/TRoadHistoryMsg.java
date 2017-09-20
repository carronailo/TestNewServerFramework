package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;
import net.netty.messages.RobTreasureRobHistory;

/**
 * Created by CarroNailo on 2017/9/19 17:20 for TestNewServerFramework.
 */
@RPC(CID = CID.TreasureRoad, MID = MID.ServerSide.TREASUREROAD_TRoadHistory)
public class TRoadHistoryMsg
{
	public RobTreasureRobHistory[] robTreasureRoadHistory;
}
