package net.netty.messages;

/**
 * Created by CarroNailo on 2017/9/8 16:59 for TestNewServerFramework.
 */
public class MID
{
	public class ServerSide
	{
		// LOGIN
		public static final int LOGIN_LoginReturn = 0;
		public static final int LOGIN_NoRole = 1;
		public static final int LOGIN_EnterWorld = 2;

		// CITY
		public static final int CITY_SomeoneEnterScene = 2;

		// Character

		// ECHO
		public static final int ECHO_Echo = 1;
	}

	public class ClientSide
	{
		// LOGIN
		public static final int LOGIN_Login = 0;
		public static final int LOGIN_CreateRole = 1;
		public static final int LOGIN_Register = 2;

		// CITY
		public static final int CITY_EnterScene = 0;

		// Character
		public static final int CHARACTER_RequestDetail = 10;

		// ECHO
		// Client side
		public static final int ECHO_EchoReturn = 1;
	}
}
