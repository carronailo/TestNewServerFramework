package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/6 18:54 for TestNewServerFramework.
 */
@RPC(CID = CID.LOGIN, MID = MID.ServerSide.LOGIN_LoginReturn)
public class LoginReturnMsg
{
	public int returnValue;
}
