package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.LOGIN, MID = MID.ClientSide.LOGIN_Login)
public class LoginMsg
{
	public String userName;
	public String password;
	public int isAdult;
	public int serverID;
	public String deviceIdentifier;
	public String deviceModel;
}
