package net.netty;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.SocketChannel;

/**
 * Created by CarroNailo on 2017/9/7 14:33 for TestNewServerFramework.
 */
public class ExtendedNioSocketChannel extends NioSocketChannel
{
	public String username;
	public int templateID;
}
