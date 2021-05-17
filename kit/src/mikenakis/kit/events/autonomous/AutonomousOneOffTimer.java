package mikenakis.kit.events.autonomous;

import mikenakis.kit.events.OneOffTimer;
import mikenakis.kit.functional.Procedure1;

import java.time.Instant;

class AutonomousOneOffTimer extends OneOffTimer implements Comparable<AutonomousOneOffTimer>
{
	private static int sequenceNumberSeed = 0;

	private final int sequenceNumber = sequenceNumberSeed++;
	final Instant timeToFire;
	private final Procedure1<AutonomousOneOffTimer> removalMethod;
	private boolean closed;

	AutonomousOneOffTimer( Instant timeToFire, Procedure1<AutonomousOneOffTimer> removalMethod )
	{
		this.timeToFire = timeToFire;
		this.removalMethod = removalMethod;
	}

	void fire()
	{
		assert !closed;
		onTick();
		if( !closed )
			close();
	}

	@Override protected void onClose()
	{
		assert !closed;
		removalMethod.invoke( this );
		closed = true;
		super.onClose();
	}

	@Override public int compareTo( AutonomousOneOffTimer other )
	{
		if( this == other )
			return 0;
		int d = timeToFire.compareTo( other.timeToFire );
		if( d != 0 )
			return d;
		//make sure that two timers with identical time-to-fire are not considered equal.
		//(Otherwise the second one will vanish when they are both added to a set.)
		return Integer.compare( sequenceNumber, other.sequenceNumber );
	}
}
