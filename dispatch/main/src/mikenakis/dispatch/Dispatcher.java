package mikenakis.dispatch;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.bespoke.Subscription;

public interface Dispatcher
{
	/**
	 * Obtains the {@link DispatcherProxy} of this {@link Dispatcher}.
	 */
	DispatcherProxy proxy();

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
	 *
	 * The {@link Dispatcher} will quit after the processing of the current event has ended,
	 * and possibly also after all events in the current event burst have been processed.
	 */
	void quit();

	/**
	 * Checks whether this {@link Dispatcher} is running.
	 */
	boolean isRunning();

	/**
	 * Causes a handler to be invoked once on the next idle event, and then automatically un-subscribed, for convenience.
	 */
	default void onNextIdle( Procedure0 handler )
	{
		assert mutationContext().inContextAssertion();
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
	 * Obtains the {@link MutationContext} of this {@link Dispatcher}.
	 */
	MutationContext mutationContext();

	/**
	 * Asserts that we are currently executing within the {@link MutationContext} of this dispatcher.
	 */
	default boolean isInContextAssertion()
	{
		assert mutationContext().inContextAssertion();
		return true;
	}

	/**
	 * Asserts that it is safe to perform read operations in the {@link MutationContext} of this dispatcher.
	 */
	default boolean canReadAssertion()
	{
		assert mutationContext().canReadAssertion();
		return true;
	}

	/**
	 * Asserts that it is safe to perform mutation operations in the {@link MutationContext} of this dispatcher.
	 */
	default boolean canMutateAssertion()
	{
		assert mutationContext().canMutateAssertion();
		return true;
	}
}
