package mikenakis.kit.events;

import mikenakis.kit.Multicast;
import mikenakis.kit.Multicaster;
import mikenakis.kit.ThreadGuard;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents an event driven system.
 * Otherwise known as "event loop", "message loop", "message pump", "dispatcher".
 */
public abstract class EventDriver
{
	/**
	 * Can be used from outside of this {@link EventDriver}'s thread to execute code within this {@link EventDriver}'s thread.
	 */
	public final class Proxy
	{
		private final Poster poster;
		private final Duration defaultTimeout;

		public Proxy( Poster poster, Duration defaultTimeout )
		{
			this.poster = poster;
			this.defaultTimeout = defaultTimeout;
		}

		/**
		 * Obtains the {@link ThreadGuard} of the proxied {@link EventDriver}.
		 */
		public final ThreadGuard threadGuard()
		{
			return threadGuard;
		}

		/**
		 * Causes a given {@link Procedure0} to be invoked in this {@link EventDriver}'s thread.
		 * The calling thread must not be this {@link EventDriver}'s thread.
		 */
		public final void post( Procedure0 procedure )
		{
			assert threadGuard.outOfThreadAssertion();
			poster.post( procedure );
		}

		/**
		 * Causes a given {@link Procedure0} to be invoked in this {@link EventDriver}'s thread and waits for it to finish.
		 * If the {@link Procedure0} does not return within the default timeout, a {@link TimeoutException} is thrown.
		 * The calling thread must not be this {@link EventDriver}'s thread.
		 */
		public final void invoke( Procedure0 procedure )
		{
			invoke( Optional.empty(), procedure );
		}

		/**
		 * Causes a given {@link Function0} to be invoked in this {@link EventDriver}'s thread, waits for it to finish, and
		 * returns the result of the {@link Function0}.
		 * If the {@link Function0} does not return within the default timeout, a {@link TimeoutException} is thrown.
		 * The calling thread must not be this {@link EventDriver}'s thread.
		 */
		public final <R> R invoke( Function0<R> function )
		{
			return invoke( Optional.empty(), function );
		}

		/**
		 * Same as {@link #invoke(Procedure0)} but with a given timeout instead of the default timeout.
		 */
		public final void invoke( Duration timeout, Procedure0 procedure )
		{
			invoke( Optional.of( timeout ), procedure );
		}

		/**
		 * Same as {@link #invoke(Function0)} but with a given timeout instead of the default timeout.
		 */
		public final <R> R invoke( Duration timeout, Function0<R> function )
		{
			return invoke( Optional.of( timeout ), function );
		}

		/**
		 * TODO: instead of using a deadlock guard, use the deadlock detection logic to detect that two threads are effectively joined during invoke()
		 * and allow reverse invoke() to pass control directly to the target thread, bypassing the post-and-wait mechanism.
		 */
		private void invoke( Optional<Duration> timeout, Procedure0 procedure )
		{
			assert threadGuard.outOfThreadAssertion();
			DeadlockGuard.instance.guard( threadGuard.thread, () -> //
			{
				CountDownLatch latch = new CountDownLatch( 1 );
				poster.post( () -> //
				{
					procedure.invoke();
					latch.countDown();
				} );
				long milliseconds = timeout.orElse( defaultTimeout ).toMillis();
				boolean success = Kit.unchecked( () -> latch.await( milliseconds, TimeUnit.MILLISECONDS ) );
				if( !success )
					throw new TimeoutException();
				assert latch.getCount() == 0;
			} );
		}

		private <R> R invoke( Optional<Duration> timeout, Function0<R> function )
		{
			AtomicReference<R> resultReference = new AtomicReference<>();
			invoke( timeout, () -> //
			{
				R result = function.invoke();
				resultReference.set( result );
			} );
			return resultReference.get();
		}
	}

	public final ThreadGuard threadGuard = ThreadGuard.create();
	protected final Multicaster idleEventMulticaster = new Multicaster();
	protected final Multicaster quitEventMulticaster = new Multicaster();

	protected EventDriver()
	{
	}

	/**
	 * Obtains the {@link Proxy} of this {@link EventDriver}.
	 * Can be invoked from any thread.
	 */
	public abstract Proxy proxy();

	/**
	 * Obtains the 'idle' {@link Multicast}.
	 * Use this to add handlers to be invoked next time this {@link EventDriver} becomes idle.
	 * Each handler will be invoked only once, and then automatically removed.
	 * If an idle handler adds a new idle handler, the new handler is guaranteed to not be invoked immediately, but on the next idle event instead.
	 */
	public final Multicast idleEventMulticast()
	{
		assert threadGuard.inThreadAssertion();
		return idleEventMulticaster.multicast;
	}

	/**
	 * Obtains the 'quit' {@link Multicast}.
	 * Use this to add handlers to be invoked when this {@link EventDriver} quits.
	 * Each handler will be invoked only once, and then automatically removed.
	 * If a quit handler adds a new quit handler, the new handler is guaranteed to not be invoked immediately. (Maybe on the next quit event?)
	 */
	public final Multicast quitEventMulticast()
	{
		return quitEventMulticaster.multicast;
	}

	/**
	 * Requests this {@link EventDriver} to quit.
	 * Can be invoked from any thread.
	 */
	public abstract void postQuit();

	/**
	 * Checks whether this {@link EventDriver} is running.
	 */
	public abstract boolean isRunning();

	public abstract Clock clock();

	public abstract OneOffTimer newOneOffTimer( Duration delay );

	public final RepeatingTimer newRepeatingTimer( Duration interval )
	{
		return new RepeatingTimer( this, interval );
	}

	public final RepeatingTimer newRepeatingTimer( Duration interval, Procedure0 handler )
	{
		RepeatingTimer repeatingTimer = newRepeatingTimer( interval );
		repeatingTimer.setHandler( handler );
		return repeatingTimer;
	}

	public final OneOffTimer newOneOffTimer( Duration delay, Procedure0 handler )
	{
		OneOffTimer oneOffTimer = newOneOffTimer( delay );
		oneOffTimer.setHandler( handler );
		return oneOffTimer;
	}

	public final void after( Duration interval, Procedure0 handler )
	{
		OneOffTimer oneOffTimer = newOneOffTimer( interval );
		Procedure0 onShutDown = () -> oneOffTimer.close();
		quitEventMulticast().add( onShutDown );
		oneOffTimer.setHandler( () -> {
			oneOffTimer.close();
			quitEventMulticast().remove( onShutDown );
			handler.invoke();
		} );
	}
}
