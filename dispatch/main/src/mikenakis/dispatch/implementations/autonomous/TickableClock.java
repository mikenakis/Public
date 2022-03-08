package mikenakis.dispatch.implementations.autonomous;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * A clock which, when ticked, obtains the current time from another clock, and keeps reporting that time as the current time until it is ticked again.
 */
final class TickableClock extends Clock
{
	private final Clock originalClock;
	private Instant currentTime;

	TickableClock( Clock originalClock )
	{
		this.originalClock = originalClock;
		currentTime = originalClock.instant();
	}

	void tick()
	{
		currentTime = originalClock.instant(); // the time stays same while processing a tick.
	}

	@Override public ZoneId getZone()
	{
		return originalClock.getZone();
	}

	@Override public Clock withZone( ZoneId zoneId )
	{
		return new TickableClock( originalClock.withZone( zoneId ) );
	}

	@Override public Instant instant()
	{
		return currentTime;
	}

	@Override public String toString()
	{
		return "TickClock[" + instant() + "," + getZone() + "]";
	}
}
