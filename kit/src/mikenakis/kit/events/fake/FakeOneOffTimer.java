package mikenakis.kit.events.fake;

import mikenakis.kit.events.OneOffTimer;

import java.time.Instant;

class FakeOneOffTimer extends OneOffTimer implements Comparable<FakeOneOffTimer>
{
	private static int idSeed = 0;

	private final int id = idSeed++;
	private final FakeEventDriver fakeEventDriver;
	final Instant timeToFire;

	public FakeOneOffTimer( FakeEventDriver fakeEventDriver, Instant timeToFire )
	{
		this.fakeEventDriver = fakeEventDriver;
		this.timeToFire = timeToFire;
	}

	void fire()
	{
		onTick();
	}

	@Override public int compareTo( FakeOneOffTimer other )
	{
		if( this == other )
			return 0;
		int d = timeToFire.compareTo( other.timeToFire );
		if( d != 0 )
			return d;
		//make sure that two timers with identical time-to-fire are not considered equal.
		//(Otherwise the second one will vanish when they are both added to a set.)
		return Integer.compare( id, other.id );
	}

	@Override protected void onClose()
	{
		fakeEventDriver.remove( this );
		super.onClose();
	}
}
