package net.netty.messages;

/**
 * Created by CarroNailo on 2017/9/19 17:21 for TestNewServerFramework.
 */
public class RobTreasureRobHistory
{
	public long roleID;
	public String oppoNick;
	public int oppoTemplateID;
	public long challengeTime;
	public int oppoLevel;
	public byte militaryExploitsType;//类型 10 挑战失败11挑战胜利 20防守失败 21防守胜利
	public int itemTemplateID;
	public byte itemNumber;
}
