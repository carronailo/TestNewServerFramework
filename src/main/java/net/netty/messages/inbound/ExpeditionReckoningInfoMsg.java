package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 16:09 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ServerSide.BATTLE_ExpeditionReckoningInfo)
public class ExpeditionReckoningInfoMsg
{
	public byte result;
}
