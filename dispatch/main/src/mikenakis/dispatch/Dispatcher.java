package mikenakis.dispatch;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.logging.Log;
import mikenakis.kit.ref.Ref;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Allows code from outside an event-driven system to execute functions within the event driven system.
 */
public interface Dispatcher
{
	/**
	 * Invokes a method in the context of the {@link EventDriver}.
	 *
	 * @param procedure0 the method to invoke.
	 */
	void post( Procedure0 procedure0 );

	/**
	 * Invokes a method in the context of the {@link EventDriver} and waits for it to finish.
	 *
	 * @param procedure the {@link Procedure0} to invoke.
	 */
	default void call( Procedure0 procedure )
	{
		CountDownLatch latch = new CountDownLatch( 1 );
		post( () -> //
		{
			procedure.invoke();
			latch.countDown();
		} );
		for( ; ; )
		{
			if( Kit.unchecked( () -> latch.await( 1000, TimeUnit.MILLISECONDS ) ) )
				break;
			Log.warning( "waiting..." );
		}
		assert latch.getCount() == 0;
	}

	/**
	 * Invokes a method in the context of the {@link EventDriver}, waits for it to finish, and returns the result.
	 *
	 * @param function the {@link Function0} to invoke.
	 * @param <R>      the type of the return value of the function.
	 *
	 * @return the return value of the function.
	 */
	default <R> R call( Function0<R> function )
	{
		Ref<R> resultRef = Ref.of( null );
		CountDownLatch latch = new CountDownLatch( 1 );
		post( () -> //
		{
			resultRef.value = function.invoke();
			latch.countDown();
		} );
		for( ; ; )
		{
			if( Kit.unchecked( () -> latch.await( 1000, TimeUnit.MILLISECONDS ) ) )
				break;
			Log.warning( "waiting..." );
		}
		assert latch.getCount() == 0;
		return resultRef.value;
	}
}
