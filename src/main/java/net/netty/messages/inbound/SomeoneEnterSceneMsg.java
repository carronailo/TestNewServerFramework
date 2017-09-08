package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/7 17:47 for TestNewServerFramework.
 */
@RPC(CID = CID.CITY, MID = MID.ServerSide.CITY_SomeoneEnterScene)
public class SomeoneEnterSceneMsg
{
	public long roleID;
	public short roleTemplateID;
	public int[] equipIDs;
	public int fashionID;
	//public int wingID;
	public short x;
	public short y;
	public short z;
	public byte vipLv;
	public String nick;
	public int petTemplateId;
}
