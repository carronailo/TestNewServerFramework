package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/7 12:04 for TestNewServerFramework.
 */
@RPC(CID = CID.LOGIN, MID = MID.ServerSide.LOGIN_EnterWorld)
public class EnterWorldMsg
{
	public long roleID;
	public int roleTemplateID;
	public String nickName;
}
