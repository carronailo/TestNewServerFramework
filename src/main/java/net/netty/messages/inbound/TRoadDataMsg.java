package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.ItemData;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 16:32 for TestNewServerFramework.
 */
@RPC(CID = CID.TreasureRoad, MID = MID.ServerSide.TREASUREROAD_TRoadData)
public class TRoadDataMsg
{
	public int leftCount;   // 剩余挑战次数
	public int allCount;    // 总的挑战次数
	public int itemState;//  1000     领取状态
	public int currChallengeChance;   //金币刷新剩余次数
	public int maxChallengeChance;    //金币刷新总次数
	public int goldRefreshNumber;   //金币数量
	public ItemData[] items;
}
