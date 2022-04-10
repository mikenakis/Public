package mikenakis.dispatch;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.logging.Log;
import mikenakis.kit.ref.Ref;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Allows using some functionality of a {@link Dispatcher} from a different thread.
 */
public interface DispatcherProxy
{
	/**
	 * Invokes a method in the context of the {@link Dispatcher}.
	 *
	 * @param procedure0 the method to invoke.
	 */
	void post( Procedure0 procedure0 );

	/**
	 * Invokes a method in the context of the {@link Dispatcher}, waits for it to finish, and returns the result.
	 *
	 * @param function the function to invoke.
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
