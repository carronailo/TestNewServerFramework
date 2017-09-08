package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/7 18:48 for TestNewServerFramework.
 */
@RPC(CID = CID.ECHO, MID = MID.ServerSide.ECHO_Echo)
public class EchoMsg
{
	public int Index;
	public long Time;
}
