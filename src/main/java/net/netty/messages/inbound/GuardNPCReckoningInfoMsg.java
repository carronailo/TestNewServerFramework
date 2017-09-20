package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.ItemData;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 15:20 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ServerSide.BATTLE_GuardNPCReckoningInfo)
public class GuardNPCReckoningInfoMsg
{
	public int gold;//金币
	public int exp;//金币
	public ItemData[] items;//奖励
}
