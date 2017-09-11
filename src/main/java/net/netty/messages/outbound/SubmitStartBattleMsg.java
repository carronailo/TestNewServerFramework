package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/11 12:42 for TestNewServerFramework.
 */
@RPC(CID = CID.BATTLE, MID = MID.ClientSide.BATTLE_SubmitStartBattle)
public class SubmitStartBattleMsg
{
}
