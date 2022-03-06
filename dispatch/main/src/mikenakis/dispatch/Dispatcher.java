package mikenakis.dispatch;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.bespoke.Subscription;

public interface Dispatcher
{
	/**
	 * Obtains the {@link MutationContext} of this {@link Dispatcher}.
	 */
	MutationContext mutationContext();

	default boolean isInContextAssertion()
	{
		assert mutationContext().isInContextAssertion();
		return true;
	}

	default boolean canReadAssertion()
	{
		assert mutationContext().canReadAssertion();
		return true;
	}

	default boolean canMutateAssertion()
	{
		assert mutationContext().canMutateAssertion();
		return true;
	}

	/**
	 * Adds a subscriber to the 'quit' event.
	 * Can only be invoked in-context.
	 * Use this to add handlers to be invoked when this {@link Dispatcher} quits.
	 */
	Subscription<Procedure0> newQuitEventSubscription( Procedure0 subscriber );

	/**
	 * Adds a subscriber to the 'idle' event.
	 * Can only be invoked in-context.
	 * Use this to add handlers to be invoked when this {@link Dispatcher} becomes idle.
	 */
	Subscription<Procedure0> newIdleEventSubscription( Procedure0 subscriber );

	/**
	 * Requests this {@link Dispatcher} to quit.
	 * Can be invoked either in-context or out-of-context.
	 */
	void quit();

	/**
	 * Checks whether this {@link Dispatcher} is running.
	 * Can be invoked either in-context or out-of-context.
	 */
	boolean isRunning();

	/**
	 * Registers a handler to be invoked once on the next idle event.
	 * Can only be invoked in-context.
	 */
	default void onNextIdle( Procedure0 handler )
	{
		assert mutationContext().isInContextAssertion();
		final class AutoRemovableHandler implements Procedure0
		{
			private final Procedure0 delegee;
			private final Subscription<Procedure0> subscription;

			private AutoRemovableHandler( Procedure0 delegee )
			{
				this.delegee = delegee;
				subscription = newIdleEventSubscription( this );
			}

			@Override public void invoke()
			{
				subscription.close();
				delegee.invoke();
			}
		}
		new AutoRemovableHandler( handler );
	}

	/**
	 * Obtains the {@link DispatcherProxy} for this {@link Dispatcher}.
	 */
	DispatcherProxy proxy();
}
