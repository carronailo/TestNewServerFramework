package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/18 14:48 for TestNewServerFramework.
 */
@RPC(CID = CID.WORLDBOSS, MID = MID.ClientSide.WORLD_BOSS_SubmitHurtValue)
public class WorldBossSubmitHurtValueMsg
{
	public int hurtValue;
	public int battleTime;
	public int hpRemain;
	public int attack;
	public int defense;
	public int[] playerAttributes;
	public int[] turnAttributes;
}
