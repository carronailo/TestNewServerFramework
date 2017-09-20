package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 15:54 for TestNewServerFramework.
 */
@RPC(CID = CID.Expedition, MID = MID.ServerSide.EXPEDITION_ExpeditionData)
public class ExpeditionDataMsg
{
	public short customsPassData;//关卡数据（通关的最后关卡）
	public short chestBoxData;//宝箱数据
	public short canResetCount;//可以重置次数
	public int playerHp;//玩家当前血量
	public long[] usedFriend;//使用过的好友列表
	public short die;//玩家是否死亡(1：死亡，0：未死亡)
	public int lessResetCountType;//缺少重置次数的类型 0-VIP等级不足，前去充值    1-是否花费XX钻石进行重置  2-重置次数已经用完
	public int resetCostDiamond;//重置花费的宝石
	public int[] chestBoxReckoningId;//宝箱结算id
}
