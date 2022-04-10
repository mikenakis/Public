package mikenakis_dispatch_test;

import mikenakis.dispatch.EventDriver;
import mikenakis.dispatch.Dispatcher;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

final class Echo implements Runnable
{
	private final EventDriver eventDriver;
	private final Dispatcher dispatcher;
	private final Thread thread;
	private final BlockingQueue<Procedure0> queue = new LinkedBlockingQueue<>();
	private boolean running;

	Echo( EventDriver eventDriver )
	{
		this.eventDriver = eventDriver;
		dispatcher = eventDriver.dispatcher();
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
		assert eventDriver.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			running = false;
		} );
	}

	void callBack( Procedure0 receiver )
	{
		assert Thread.currentThread() != thread;
		assert eventDriver.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			dispatcher.call( () -> //
			{
				assert Thread.currentThread() != thread;
				assert eventDriver.isInContextAssertion();
				receiver.invoke();
				return null;
			} );
		} );
	}

	void postBack( Procedure0 receiver )
	{
		assert Thread.currentThread() != thread;
		assert eventDriver.isInContextAssertion();
		queue.add( () -> //
		{
			assert Thread.currentThread() == thread;
			dispatcher.post( () -> //
			{
				assert Thread.currentThread() != thread;
				assert eventDriver.isInContextAssertion();
				receiver.invoke();
			} );
		} );
	}
}
