package mikenakis.kit.events.fake;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

final class FakeClock extends Clock
{
	Instant currentTime;
	private final ZoneId zoneId;

	FakeClock( ZoneId zoneId, Instant currentTime )
	{
		this.zoneId = zoneId;
		this.currentTime = currentTime;
	}

	@Override public ZoneId getZone()
	{
		return zoneId;
	}

	@Override public Clock withZone( ZoneId zoneId )
	{
		return new FakeClock( zoneId, currentTime );
	}

	@Override public Instant instant()
	{
		return currentTime;
	}

	@Override public String toString()
	{
		return "FakeClock[" + instant() + "," + getZone() + "]";
	}
}
