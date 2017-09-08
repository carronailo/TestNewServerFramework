package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.LOGIN, MID = MID.ClientSide.LOGIN_CreateRole)
public class CreateRoleMsg
{
	public String roleName;
	public int templateID;
}
