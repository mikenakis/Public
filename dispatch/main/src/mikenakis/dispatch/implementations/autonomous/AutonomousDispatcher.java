package mikenakis.dispatch.implementations.autonomous;

import mikenakis.dispatch.Dispatcher;
import mikenakis.dispatch.DispatcherProxy;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.bespoke.Publisher;
import mikenakis.publishing.bespoke.Subscription;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class AutonomousDispatcher extends Mutable implements Dispatcher, Closeable.Defaults
{
	public static AutonomousDispatcher of( MutationContext mutationContext, Clock clock )
	{
		return new AutonomousDispatcher( mutationContext, clock );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Publisher<Procedure0> idleEventPublisher = Publisher.of( mutationContext, Procedure0.class );
	private final Publisher<Procedure0> quitEventPublisher = Publisher.of( mutationContext, Procedure0.class );
	private final DispatcherProxy proxy = new MyDispatcherProxy();
	private final TickableClock tickableClock;
	private final Thread dispatcherThread;
	private final BlockingQueue<Procedure0> queue = new LinkedBlockingQueue<>();
	private boolean running;
	private boolean entered;
	private Instant timeToUnblock = null;

	private AutonomousDispatcher( MutationContext mutationContext, Clock clock )
	{
		super( mutationContext );
		tickableClock = new TickableClock( clock );
		dispatcherThread = Thread.currentThread();
	}

	@Override public boolean isAliveAssertion()
	{
		assert canReadAssertion();
		return lifeGuard.isAliveAssertion();
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		assert !running;
		idleEventPublisher.close();
		quitEventPublisher.close();
		lifeGuard.close();
	}

	public Clock clock()
	{
		return tickableClock;
	}

	public void setTimeToUnblock( Instant instant )
	{
		assert canMutateAssertion();
		assert instant != null;
		assert !instant.isBefore( tickableClock.instant() );
		timeToUnblock = instant;
	}

	public void run()
	{
		assert canMutateAssertion();
		assert !entered;
		entered = true;
		assert !running;
		for( running = true; running; )
			pumpMessageBurst();
		invoke( () -> quitEventPublisher.allSubscribers().invoke() );
		entered = false;
	}

	private void pumpMessageBurst()
	{
		tickableClock.tick();
		for( ; ; )
		{
			pumpSingleMessage().ifPresent( Procedure0::invoke );
			if( queue.isEmpty() )
				break;
		}
		invoke( () -> idleEventPublisher.allSubscribers().invoke() );
	}

	private Optional<Procedure0> pumpSingleMessage()
	{
		if( timeToUnblock == null )
		{
			Procedure0 procedure = Kit.unchecked( () -> queue.take() );
			assert procedure != null;
			return Optional.of( procedure );
		}
		Duration duration = Duration.between( tickableClock.instant(), timeToUnblock );
		timeToUnblock = null;
		Procedure0 procedure = Kit.unchecked( () -> queue.poll( duration.toMillis(), TimeUnit.MILLISECONDS ) );
		return Optional.ofNullable( procedure );
	}

	private void invoke( Procedure0 procedure )
	{
		procedure.invoke();
	}

	@Override public DispatcherProxy proxy()
	{
		return proxy;
	}

	@Override public Subscription<Procedure0> newQuitEventSubscription( Procedure0 subscriber )
	{
		assert canMutateAssertion();
		return quitEventPublisher.addSubscription( subscriber );
	}

	@Override public Subscription<Procedure0> newIdleEventSubscription( Procedure0 subscriber )
	{
		assert canMutateAssertion();
		return idleEventPublisher.addSubscription( subscriber );
	}

	@Override public void quit()
	{
		assert canMutateAssertion();
		running = false;
	}

	@Override public boolean isRunning()
	{
		return running;
	}

	private class MyDispatcherProxy implements DispatcherProxy
	{
		@Override public boolean outOfContextAssertion()
		{
			assert Thread.currentThread() != dispatcherThread;
			return true;
		}

		@Override public void post( Procedure0 procedure )
		{
			assert outOfContextAssertion();
			queue.add( procedure );
		}
	}
}
