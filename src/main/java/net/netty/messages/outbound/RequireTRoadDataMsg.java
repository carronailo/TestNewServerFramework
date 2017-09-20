package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 16:31 for TestNewServerFramework.
 */
@RPC(CID = CID.TreasureRoad, MID = MID.ClientSide.TREASUREROAD_RequireTRoadData)
public class RequireTRoadDataMsg
{
}
