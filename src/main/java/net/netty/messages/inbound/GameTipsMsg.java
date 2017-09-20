package net.netty.messages.inbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

/**
 * Created by CarroNailo on 2017/9/20 15:15 for TestNewServerFramework.
 */
@RPC(CID = CID.TIPS, MID = MID.ServerSide.TIPS_GameTips)
public class GameTipsMsg
{
	public String tipsStr;
}
