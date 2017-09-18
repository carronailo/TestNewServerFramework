package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.TOWERUP, MID = MID.ServerSide.TOWER_UP_TowerUpData)
public class TowerUpDataMsg
{
	public short record;//历史记录
	public short curFloor;//当前层
	public short resetCount;//可重置次数
	public short havaAutoFightAward;//是否有自动战斗奖励 1有，0没有
	public short leftRaisedUpCount;//剩余复活次数
	public short raisedUpCostDiamondCount;//复活消耗钻石数量
}
