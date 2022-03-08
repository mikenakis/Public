package mikenakis.dispatch.implementations.autonomous;

import mikenakis.dispatch.Timekeeper;
import mikenakis.dispatch.Timer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.publishing.bespoke.Subscription;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.MutableCollections;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class AutonomousTimekeeper extends Mutable implements Timekeeper, Closeable.Defaults
{
	public static AutonomousTimekeeper of( MutationContext mutationContext, AutonomousDispatcher dispatcher, Clock clock )
	{
		return new AutonomousTimekeeper( mutationContext, dispatcher, clock );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AutonomousDispatcher autonomousDispatcher;
	private final Clock clock;
	private final MutableCollection<MyTimer> timers = MutableCollections.of( mutationContext ).newLinkedHashSet();
	private final Subscription<Procedure0> idleEventSubscription;

	private AutonomousTimekeeper( MutationContext mutationContext, AutonomousDispatcher autonomousDispatcher, Clock clock )
	{
		super( mutationContext );
		this.autonomousDispatcher = autonomousDispatcher;
		this.clock = clock;
		idleEventSubscription = autonomousDispatcher.newIdleEventSubscription( this::onIdle );
	}

	@Override public boolean isAliveAssertion()
	{
		assert canReadAssertion();
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		assert timers.isEmpty();
		idleEventSubscription.close();
		lifeGuard.close();
	}

	@Override public Clock clock()
	{
		assert isAliveAssertion();
		return clock;
	}

	@Override public Timer newTimer( Duration period, Timer.Tick timerTick )
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		return new MyTimer( period, timerTick );
	}

	private void onIdle()
	{
		assert isAliveAssertion();
		for( var timer : timers.toList() )
			timer.onIdle();
		Instant minTimeToFire = null;
		for( var timer : timers )
		{
			Instant timeToFire = timer.timeToFire();
			if( minTimeToFire == null || timeToFire.isBefore( minTimeToFire ) )
				minTimeToFire = timeToFire;
		}
		if( minTimeToFire != null )
		{
			Instant timeNow = clock.instant();
			if( minTimeToFire.isBefore( timeNow ) )
				minTimeToFire = timeNow;
			autonomousDispatcher.setTimeToUnblock( minTimeToFire );
		}
	}

	private class MyTimer extends Mutable implements Timer, Closeable.Defaults
	{
		private final LifeGuard lifeGuard = LifeGuard.of( this );
		private final Duration period;
		private final Timer.Tick timerTick;
		private Instant lastTime;
		private Instant timeToFire;

		protected MyTimer( Duration period, Timer.Tick timerTick )
		{
			super( AutonomousTimekeeper.this.mutationContext );
			assert period.toMillis() > 0;
			this.period = period;
			this.timerTick = timerTick;
			lastTime = clock.instant();
			timeToFire = lastTime.plus( period );
			timers.add( this );
		}

		@Override public boolean isAliveAssertion()
		{
			assert canReadAssertion();
			assert lifeGuard.isAliveAssertion();
			return true;
		}

		@Override public void close()
		{
			assert isAliveAssertion();
			assert canMutateAssertion();
			timers.remove( this );
			lifeGuard.close();
		}

		void onIdle()
		{
			assert isAliveAssertion();
			assert canMutateAssertion();
			Instant timeNow = clock.instant();
			if( timeNow.isBefore( timeToFire ) )
				return;
			Duration timeDelta = Duration.between( lastTime, timeNow );
			lastTime = timeNow;
			timerTick.tick( timeDelta );
			timeToFire = timeToFire.plus( period );
		}

		Instant timeToFire()
		{
			assert isAliveAssertion();
			return timeToFire;
		}

		@Override public Duration period()
		{
			assert isAliveAssertion();
			return period;
		}
	}
}
