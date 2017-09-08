package net.netty.messages.outbound;

import net.netty.messages.CID;
import net.netty.messages.MID;
import net.netty.messages.RPC;

@RPC(CID = CID.CHARACTER, MID = MID.ClientSide.CHARACTER_RequestDetail)
public class RequestDetailMsg
{
}
