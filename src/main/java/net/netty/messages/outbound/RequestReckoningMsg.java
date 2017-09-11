package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 12:35 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ClientSide.BATTLE_RequestReckoning)
public class RequestReckoningMsg
{
	public byte result;
	public int battleTime;
	public int hpRemain;
	public int attack;
	public int defense;
	public int[] playerAttributes;
	public int[] turnAttributes;
}
