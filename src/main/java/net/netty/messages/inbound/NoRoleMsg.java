package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/7 11:53 for TestNewServerFramework.
 */
@RPC(CID = CID.LOGIN, MID = MID.LOGIN_NoRole)
public class NoRoleMsg
{
	public long roleID;
	public int roleTemplateID;
}
