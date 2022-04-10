package mikenakis_dispatch_test;

import mikenakis.dispatch.Dispatcher;
import mikenakis.dispatch.DispatcherProxy;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class Echo implements Runnable
{
	private final Dispatcher dispatcher;
	private final DispatcherProxy dispatcherProxy;
	private final Thread thread;
	private final BlockingQueue<Procedure0> queue = new LinkedBlockingQueue<>();
	private boolean running;

	Echo( Dispatcher dispatcher )
	{
		this.dispatcher = dispatcher;
		dispatcherProxy = dispatcher.proxy();
		thread = Thread.currentThread();
	}

	@Override public void run()
	{
		assert Thread.currentThread() == thread;
		for( running = true; running; )
		{
			Procedure0 procedure = Kit.unchecked( () -> queue.take() );
			procedure.invoke();
		}
	}

	void quit()
	{
		assert Thread.currentThread() != thread;
		assert dispatcher.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			running = false;
		} );
	}

	void callBack( Procedure0 receiver )
	{
		assert Thread.currentThread() != thread;
		assert dispatcher.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			dispatcherProxy.call( () -> //
			{
				assert Thread.currentThread() != thread;
				assert dispatcher.isInContextAssertion();
				receiver.invoke();
				return null;
			} );
		} );
	}

	void postBack( Procedure0 receiver )
	{
		assert Thread.currentThread() != thread;
		assert dispatcher.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			dispatcherProxy.post( () -> //
			{
				assert Thread.currentThread() != thread;
				assert dispatcher.isInContextAssertion();
				receiver.invoke();
			} );
		} );
	}
}
