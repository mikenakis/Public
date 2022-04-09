package mikenakis_dispatch_test;

import mikenakis.dispatch.Dispatcher;
import mikenakis.dispatch.implementations.autonomous.AutonomousDispatcher;
import mikenakis.kit.Kit;
import mikenakis.kit.logging.Log;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.ThreadLocalMutationContext;
import org.junit.Test;

import java.time.Clock;

/**
 * test.
 *
 * @author michael.gr
 */
public class T011_Call
{
	private final MutationContext mutationContext = ThreadLocalMutationContext.instance();
	private Thread2<Echo> thread2;
	private Dispatcher dispatcher;

	@Test
	public void Call_Works()
	{
		assert mutationContext.inContextAssertion();
		Kit.tryWith( AutonomousDispatcher.of( mutationContext, Clock.systemUTC() ), dispatcher -> //
		{
			this.dispatcher = dispatcher;
			thread2 = new Thread2<>( () -> new Echo( dispatcher ) );
			thread2.worker().postBack( this::call1 );
			dispatcher.run();
			thread2.worker().quit();
			Kit.unchecked( () -> thread2.join() );
		} );
	}

	private void call1()
	{
		Log.debug( "call1" );
		assert mutationContext.inContextAssertion();
		thread2.worker().callBack( this::call2 );
	}

	private void call2()
	{
		Log.debug( "call2" );
		assert mutationContext.inContextAssertion();
		thread2.worker().callBack( this::call3 );
	}

	private void call3()
	{
		Log.debug( "call3" );
		assert mutationContext.inContextAssertion();
		dispatcher.quit();
	}
}
