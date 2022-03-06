package mikenakis_dispatch_test;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;

final class Thread2<T extends Runnable> extends Thread
{
	private final Function0<T> workerFactory;
	private volatile T worker;

	Thread2( Function0<T> workerFactory )
	{
		this.workerFactory = workerFactory;
		start();
		while( worker == null )
			Kit.unchecked( () -> Thread.yield() );
	}

	T worker()
	{
		return worker;
	}

	@Override public void run()
	{
		worker = workerFactory.invoke();
		worker.run();
	}
}
