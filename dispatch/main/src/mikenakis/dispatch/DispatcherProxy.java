package mikenakis.dispatch;

import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;

/**
 * Allows using some functionality of a {@link Dispatcher} from a different thread.
 */
public interface DispatcherProxy
{
	/**
	 * Asserts that we are NOT running in the context of the {@link Dispatcher}.
	 */
	boolean outOfContextAssertion();

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
	<R> R call( Function0<R> function );
}
