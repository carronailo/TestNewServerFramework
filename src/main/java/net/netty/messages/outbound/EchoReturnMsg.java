package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.ECHO, MID = MID.ClientSide.ECHO_EchoReturn)
public class EchoReturnMsg
{
	public int index;
	public long time;
	public String key;
}
