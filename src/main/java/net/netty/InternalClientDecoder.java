package net.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.netty.messages.*;

import java.util.List;

/**
 * Created by CarroNailo on 2017/9/6 18:29 for TestNewServerFramework.
 */
public class InternalClientDecoder extends ByteToMessageDecoder
{
//	private String username;
//	private int templateID;
//
//	public InternalClientDecoder(int index, String username, int templateID)
//	{
//		this.username = username;
//		this.templateID = templateID;
//	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
	{
		if (in.readableBytes() > 2)
		{
			int pos = in.readerIndex();
			int len = in.readShort();
			if (in.readableBytes() >= len)
			{
				byte cid = in.readByte();
				byte mid = in.readByte();
				ByteBuf content = in.readBytes(len - 2);
				Object msg = DecodeByID(ctx, cid, mid, content);
				content.release();
				if (msg != null)
					out.add(msg);
				else
					out.add(new Object());
			}
			else
				in.readerIndex(pos);
		}
	}

	Object DecodeByID(ChannelHandlerContext ctx, int cid, int mid, ByteBuf content)
	{
		Channel channel = ctx.channel();
		if (channel instanceof ExtendedNioSocketChannel)
		{
			String username = ((ExtendedNioSocketChannel) channel).username;
			switch (cid)
			{
				case 0:    // 登录
					switch (mid)
					{
						case 0:        // 登录返回
							return DecodeLoginReturn(username, content);
						case 1:
							return DecodeNoRole(username, content);
						case 2:
							return DecodeEnterWorld(username, content);
					}
					break;
				case 1:
					switch (mid)
					{
						case 2:
							return DecodeEnterScene(username, content);
					}
					break;
				case 37:
					if (mid >= 2 && mid <= 5)
						return new SecurityMsg();
					else if (mid >= 20 && mid <= 25)
						return new SHA1Msg();
					else if(mid == 1)
						return DecodeEcho(username, content);
					break;
				default:
					break;
			}
		}
		return null;
	}

	Object DecodeEcho(String username, ByteBuf content)
	{
		EchoMsg msgObj = null;
		try
		{
			msgObj = new EchoMsg();
			msgObj.Index = content.readInt();
			msgObj.Time = content.readLong();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msgObj;
	}

	Object DecodeLoginReturn(String username, ByteBuf content)
	{
		LoginReturnMsg msgObj = null;
		try
		{
			System.out.println(String.format("[%s]收到 LoginReturnMsg 消息", username));
			msgObj = new LoginReturnMsg();
			msgObj.returnValue = content.readInt();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msgObj;
	}

	Object DecodeNoRole(String username, ByteBuf content)
	{
		NoRoleMsg msgObj = null;
		try
		{
			System.out.println(String.format("[%s]收到 NoRoleMsg 消息", username));
			msgObj = new NoRoleMsg();
			msgObj.roleID = content.readLong();
			msgObj.roleTemplateID = content.readInt();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msgObj;
	}

	Object DecodeEnterWorld(String username, ByteBuf content)
	{
		EnterWorldMsg msgObj = null;
		try
		{
			System.out.println(String.format("[%s]收到 EnterWorldMsg 消息", username));
			msgObj = new EnterWorldMsg();
			msgObj.roleID = content.readLong();
			msgObj.roleTemplateID = content.readInt();
			short len = content.readShort();
			byte[] strTemp = new byte[len];
			content.readBytes(strTemp);
			msgObj.nickName = new String(strTemp, "UTF8");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msgObj;
	}

	Object DecodeEnterScene(String username, ByteBuf content)
	{
		EnterSceneMsg msgObj = null;
		try
		{
//			System.out.println(String.format("[%s]收到 EnterScene 消息", username));
			msgObj = new EnterSceneMsg();
			msgObj.roleID = content.readLong();
			msgObj.roleTemplateID = content.readShort();
			short equiplen = content.readShort();
			msgObj.equipIDs = new int[equiplen];
			for(int i = 0 ; i < equiplen; ++i)
				msgObj.equipIDs[i] = content.readInt();
			msgObj.fashionID = content.readInt();
			msgObj.x = content.readShort();
			msgObj.y = content.readShort();
			msgObj.z = content.readShort();
			msgObj.vipLv = content.readByte();
			short nicklen = content.readShort();
			byte[] strTemp = new byte[nicklen];
			content.readBytes(strTemp);
			msgObj.nick = new String(strTemp, "UTF8");
			msgObj.petTemplateId = content.readInt();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return msgObj;

	}

}
