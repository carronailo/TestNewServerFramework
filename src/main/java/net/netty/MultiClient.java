package net.netty;

import io.netty.util.concurrent.*;

/**
 * Created by CarroNailo on 2017/9/6 10:31 for TestNewServerFramework.
 */
public class MultiClient
{
	public static void main(String[] args)
	{
		MultiClient host = new MultiClient();

		int connectionNumber = 1;

		EventExecutorGroup group = new DefaultEventExecutorGroup(connectionNumber);

		Future<?>[] fs = new Future<?>[connectionNumber];
		for(int i = 0 ; i < fs.length; ++i)
		{
			final int index = i;
			fs[i] = group.submit(new Runnable()
			{
				@Override
				public void run()
				{
					host.StartClient(index);
				}
			}, null);
			fs[i].addListener(future ->
			{
				System.out.println("operationComplete");
			});
		}
		for(;;)
		{
			boolean allDone = true;
			for(Future<?> f : fs)
			{
				if(!f.isDone())
				{
					allDone = false;
					break;
				}
			}
			if(allDone)
				break;
		}
		System.out.println("All Done.");
		group.shutdownGracefully();
	}

	void StartClient(int index)
	{
		SingleClient client = new SingleClient(6868, index);

		client.Start();
	}

}
