package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/12 10:19 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ClientSide.BATTLE_RequestPVE)
public class RequestPVEMsg
{
	public int copyID;
	public byte pveType;
	public int assistItemA;
	public int assistItemB;
	public long friendID;
}
