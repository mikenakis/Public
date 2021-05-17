package mikenakis.kit.events;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.time.Duration;
import java.time.Instant;

public class RepeatingTimer implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final EventDriver eventDriver;
	private final Duration interval;
	private final Instant lastTime;
	private Procedure0 handler;
	private OneOffTimer oneOffTimer;

	RepeatingTimer( EventDriver eventDriver, Duration interval )
	{
		assert interval.compareTo( Duration.ZERO ) > 0;
		this.eventDriver = eventDriver;
		this.interval = interval;
		lastTime = eventDriver.clock().instant();
		oneOffTimer = eventDriver.newOneOffTimer( interval, this::oneOffTimerHandler );
	}

	private void oneOffTimerHandler()
	{
		assert lifeGuard.isAliveAssertion();
		assert handler != null; //the handler must be set immediately after construction!
		Instant nextTime = lastTime;
		Instant currentTime = eventDriver.clock().instant();
		while( nextTime.isBefore( currentTime ) )
			nextTime = nextTime.plus( interval );
		Duration newInterval = Duration.between( nextTime, currentTime );
		oneOffTimer.close();
		oneOffTimer = eventDriver.newOneOffTimer( newInterval, this::oneOffTimerHandler );
		handler.invoke();
	}

	public void setHandler( Procedure0 handler )
	{
		assert handler != null;
		assert this.handler == null;
		this.handler = handler;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( value );
		return true;
	}
	@Override public void close()
	{
		assert (lifeGuard.isAliveAssertion());
		oneOffTimer.close();
		lifeGuard.close();
	}
}
