package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javafx.util.Pair;
import net.netty.messages.OutBoundMessageMap;

/**
 * Created by CarroNailo on 2017/9/8 16:17 for TestNewServerFramework.
 */
public class InternalClientEncoder extends MessageToByteEncoder
{
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
	{
		Pair<Integer, Integer> idPair = OutBoundMessageMap.getInstance().getIDByMessageClass(msg.getClass());
		if(idPair != null)
		{

		}
	}
}
