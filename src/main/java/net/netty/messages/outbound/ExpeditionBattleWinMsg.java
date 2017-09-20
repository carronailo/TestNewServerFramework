package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 16:00 for TestNewServerFramework.
 */
@RPC(CID = CID.Expedition, MID = MID.ClientSide.EXPEDITION_ExpeditionBattleWin)
public class ExpeditionBattleWinMsg
{
	public int playerHPRemain;
}
