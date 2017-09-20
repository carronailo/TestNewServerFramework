package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 17:20 for TestNewServerFramework.
 */
@RPC(CID = CID.TreasureRoad, MID = MID.ClientSide.TREASUREROAD_RequireTRoadHistory)
public class RequireTRoadHistoryMsg
{
}
