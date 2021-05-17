package mikenakis.kit.events.autonomous;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

final class TickClock extends Clock
{
	private final Clock originalClock;
	Instant currentTime;

	TickClock( Clock originalClock )
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
		return new TickClock( originalClock.withZone( zoneId ) );
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
