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
public class T010_Post
{
	private final MutationContext mutationContext = ThreadLocalMutationContext.instance();
	private Thread2<Echo> thread2;
	private Dispatcher dispatcher;

	@Test
	public void Post_Works()
	{
		assert mutationContext.inContextAssertion();
		Kit.tryWith( AutonomousDispatcher.of( mutationContext, Clock.systemUTC() ), dispatcher -> //
		{
			this.dispatcher = dispatcher;
			thread2 = new Thread2<>( () -> new Echo( dispatcher ) );
			thread2.worker().postBack( this::post1 );
			dispatcher.run();
			thread2.worker().quit();
			Kit.unchecked( () -> thread2.join() );
		} );
	}

	private void post1()
	{
		Log.debug( "post1" );
		assert mutationContext.inContextAssertion();
		thread2.worker().postBack( this::post2 );
	}

	private void post2()
	{
		Log.debug( "post2" );
		assert mutationContext.inContextAssertion();
		thread2.worker().postBack( this::post3 );
	}

	private void post3()
	{
		Log.debug( "post3" );
		assert mutationContext.inContextAssertion();
		dispatcher.quit();
	}
}
