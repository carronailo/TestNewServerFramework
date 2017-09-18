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
		public static final int CITY_RequestPVEReturn = 0;
		public static final int CITY_RequestPVPReturn = 1;
		public static final int CITY_SomeoneEnterScene = 2;

		// BATTLE
		public static final int BATTLE_PVEReckoningInfo = 1;
		public static final int BATTLE_PVPReckoningInfo = 4;

		// Character

		// Arena Solo
		public static final int ARENA_SOLO_ArenaRankInfo = 0;
		public static final int ARENA_SOLO_ArenaRankList = 2;

		// ECHO
		public static final int ECHO_Echo = 1;

		// Tower Up
		public static final int TOWER_UP_TowerUpData = 0;
		public static final int TOWER_UP_TowerUpRanking = 2;

		// World Boss
		public static final int WORLD_BOSS_WorldBossData = 0;
		public static final int WORLD_BOSS_WorldBossRankData = 1;
		public static final int WORLD_BOSS_WorldBossBattleResult = 3;
	}

	public class ClientSide
	{
		// LOGIN
		public static final int LOGIN_Login = 0;
		public static final int LOGIN_CreateRole = 1;
		public static final int LOGIN_Register = 2;

		// CITY
		public static final int CITY_EnterScene = 0;

		// BATTLE
		public static final int BATTLE_RequestPVE = 0;
		public static final int BATTLE_RequestReckoning = 2;
		public static final int BATTLE_SubmitStartBattle = 14;

		// Character
		public static final int CHARACTER_RequestDetail = 10;

		// Arena Solo
		public static final int ARENA_SOLO_RequireUpdateArenaInfo = 0;
		public static final int ARENA_SOLO_RequirePKPlayer = 6;
		public static final int ARENA_SOLO_RequeireRankList = 11;

		// ECHO
		public static final int ECHO_EchoReturn = 1;

		// Tower Up
		public static final int TOWER_UP_RequireTowerUpData = 0;
		public static final int TOWER_UP_RequireTowerUpRanking = 6;

		// World Boss
		public static final int WORLD_BOSS_RequireWorldBossData = 0;
		public static final int WORLD_BOSS_RequireWorldBossRankData = 1;
		public static final int WORLD_BOSS_BeginChallenge = 2;
		public static final int WORLD_BOSS_SubmitHurtValue = 3;
		public static final int WORLD_BOSS_HurtValuePerHit = 4;
	}
}
