package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/19 15:53 for TestNewServerFramework.
 */
@RPC(CID = CID.Expedition, MID = MID.ClientSide.EXPEDITION_RequireExpeditionData)
public class RequireExpeditionDataMsg
{
}
