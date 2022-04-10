package mikenakis.dispatch;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.bespoke.Subscription;

/**
 * Represents the heart of an event-driven system, otherwise known as `message pump`, `event loop`, etc.
 *
 * Unlike other message pump implementations, the `post()` and `call()` methods are not part of this interface.
 * Instead, these methods have been moved into the {@link Dispatcher} interface, which can be obtained by invoking {@link #dispatcher()}.
 * This way, code running outside the event loop thread can be given only a {@link Dispatcher} to work with, so that it cannot invoke any methods of the
 * {@link EventDriver} interface, which only make sense to be invoked from within the event loop thread.
 */
public interface EventDriver
{
	/**
	 * Obtains the {@link Dispatcher} of this {@link EventDriver}.
	 */
	Dispatcher dispatcher();

	/**
	 * Adds a subscriber to the 'quit' event.
	 * Can only be invoked in-context.
	 * Use this to add handlers to be invoked when this {@link EventDriver} quits.
	 */
	Subscription<Procedure0> newQuitEventSubscription( Procedure0 subscriber );

	/**
	 * Adds a subscriber to the 'idle' event.
	 * Can only be invoked in-context.
	 * Use this to add handlers to be invoked when this {@link EventDriver} becomes idle.
	 */
	Subscription<Procedure0> newIdleEventSubscription( Procedure0 subscriber );

	/**
	 * Requests this {@link EventDriver} to quit.
	 *
	 * The {@link EventDriver} will quit after the processing of the current event has ended,
	 * and possibly also after all events in the current event burst have been processed.
	 */
	void quit();

	/**
	 * Checks whether this {@link EventDriver} is running.
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
	 * Obtains the {@link MutationContext} of this {@link EventDriver}.
	 */
	MutationContext mutationContext();

	/**
	 * Asserts that we are currently executing within the {@link MutationContext} of this {@link EventDriver}.
	 */
	default boolean isInContextAssertion()
	{
		assert mutationContext().inContextAssertion();
		return true;
	}

	/**
	 * Asserts that it is safe to perform read operations in the {@link MutationContext} of this {@link EventDriver}.
	 */
	default boolean canReadAssertion()
	{
		assert mutationContext().canReadAssertion();
		return true;
	}

	/**
	 * Asserts that it is safe to perform mutation operations in the {@link MutationContext} of this {@link EventDriver}.
	 */
	default boolean canMutateAssertion()
	{
		assert mutationContext().canMutateAssertion();
		return true;
	}
}
