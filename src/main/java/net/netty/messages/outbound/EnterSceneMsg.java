package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.CITY, MID = MID.ClientSide.CITY_EnterScene)
public class EnterSceneMsg
{
	public short sceneID;
	public short x;
	public short y;
	public short z;
}
