package mikenakis.dispatch.implementations.fake;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

final class FakeClock extends Clock
{
	private Instant currentTime;
	private final ZoneId zoneId;

	FakeClock( ZoneId zoneId, Instant currentTime )
	{
		this.zoneId = zoneId;
		this.currentTime = currentTime;
	}

	public void setTime( Instant time )
	{
		currentTime = time;
	}

	public void advance()
	{
		currentTime = currentTime.plus( Duration.ofMillis( 1 ) );
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
