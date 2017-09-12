package common.utility;

/**
 * Created by CarroNailo on 2017/9/12 15:10 for TestNewServerFramework.
 */
public class Pair<A,B>
{
	public final A first;
	public final B second;

	protected Pair(A first, B second)
	{
		this.first = first;
		this.second = second;
	}

	public static <A,B> Pair<A,B> makePair(A first, B second)
	{
		return new Pair<A,B>(first, second);
	}

	public String toString()
	{
		return super.toString() + "[" + first + "," + second + "]";
	}
}
