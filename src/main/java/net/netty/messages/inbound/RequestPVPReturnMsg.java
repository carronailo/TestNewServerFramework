package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.ConfigSkill;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 11:07 for TestNewServerFramework.
 */
@RPC(CID = CID.CITY, MID = MID.ServerSide.CITY_RequestPVPReturn)
public class RequestPVPReturnMsg
{
	public byte pvpType;
	public byte result;
	public byte charLevel;
	public int[] charAttributes;
	public ConfigSkill[] charSkills;
}
