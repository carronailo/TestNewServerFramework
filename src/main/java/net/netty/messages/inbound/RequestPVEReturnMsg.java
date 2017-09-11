package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.ConfigSkill;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 11:05 for TestNewServerFramework.
 */
@RPC(CID = CID.CITY, MID = MID.ServerSide.CITY_RequestPVEReturn)
public class RequestPVEReturnMsg
{
	public byte pveType;
	public byte result;
	public int copyID;
	public int assistItemA;
	public int assistItemB;
	public long friendId;
	public byte charLevel;
	public int[] charAttributes;
	public ConfigSkill[] charSkills;
	public int turnTemplateID;
	public float turnDuration;
	public float turnCoolDown;
	public int turnCount;
	public byte turnLevel;
	public int[] turnAttributes;
	public ConfigSkill[] turnSkills;
}
