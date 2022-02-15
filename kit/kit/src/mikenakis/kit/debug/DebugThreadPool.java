package mikenakis.kit.debug;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.ref.Ref;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

//TODO: implement this as an actual thread-pool instead of using a single thread?
class DebugThreadPool
{
	private static final Object lock = new Object();
	private static DebugThreadPool instance;

	static DebugThreadPool instance()
	{
		synchronized( lock )
		{
			if( instance == null )
				instance = new DebugThreadPool();
		}
		return instance;
	}


	private final BlockingQueue<Procedure0> queue = new LinkedBlockingQueue<>();
	private final Thread thread;

	private DebugThreadPool()
	{
		thread = new Thread( this::threadMethod );
		thread.start();
	}

	void post( Procedure0 procedure0 )
	{
		if( Thread.currentThread() == thread )
			procedure0.invoke();
		else
			queue.add( procedure0 );
	}

	<T> T call( Function0<T> function0 )
	{
		if( Thread.currentThread() == thread )
			return function0.invoke();
		else
		{
			CountDownLatch latch = new CountDownLatch( 1 );
			Ref<T> resultRef = new Ref<>( null );
			queue.add( () -> //
			{
				resultRef.value = function0.invoke();
				latch.countDown();
			} );
			Kit.unchecked( () -> latch.await() );
			assert latch.getCount() == 0;
			return resultRef.value;
		}
	}

	private void threadMethod()
	{
		assert Thread.currentThread() == thread;
		for( ; ; )
		{
			Procedure0 procedure0 = Kit.unchecked( queue::take );
			procedure0.invoke();
		}
	}
}
