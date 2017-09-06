package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.netty.messages.LoginReturnMsg;

import java.util.List;

/**
 * Created by CarroNailo on 2017/9/6 18:29 for TestNewServerFramework.
 */
public class InternalClientDecoder extends ByteToMessageDecoder
{
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		if (in.readableBytes() > 2)
		{
			int len = in.readShort();
			if (in.readableBytes() >= len)
			{
				byte cid = in.readByte();
				byte mid = in.readByte();
				ByteBuf content = in.readBytes(len - 2);
				Object msg = DecodeByID(cid, mid, content);
				content.release();
				if(msg != null)
					out.add(msg);
			} else
				in.resetReaderIndex();
		}
	}

	Object DecodeByID(int cid, int mid, ByteBuf content)
	{
		switch (cid)
		{
			case 0:    // 登录
				switch (mid)
				{
					case 0:		// 登录返回
						return DecodeLoginReturn(content);
				}
				break;
			default:
				break;
		}
		return null;
	}

	Object DecodeLoginReturn(ByteBuf content)
	{
		LoginReturnMsg msgObj = null;
		try
		{
			msgObj = new LoginReturnMsg();
			msgObj.returnValue = content.readInt();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();;
		}
		return msgObj;
	}

}
