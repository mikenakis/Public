package io.github.mikenakis.live.guard;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.live.StatefulMortalCoherent;

/**
 * A {@link LifeGuard} which contains an explicit open/closed state.
 *
 * @author michael.gr
 */
public final class StatefulLifeGuard extends AbstractCoherent implements LifeGuard.Defaults, StatefulMortalCoherent.Defaults
{
	public static StatefulLifeGuard of( Mortal mortal )
	{
		return of( 1, mortal, false );
	}

	public static StatefulLifeGuard of(Mortal mortal, boolean collectStackTrace )
	{
		return of( 1, mortal, collectStackTrace );
	}

	public static StatefulLifeGuard of( int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		return new StatefulLifeGuard( framesToSkip + 1, mortal, collectStackTrace );
	}

	private final LifeGuard lifeGuard;
	private boolean closed;

	private StatefulLifeGuard( int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		super( mortal.coherence() );
		lifeGuard = LifeGuard.of( framesToSkip + 1, mortal, collectStackTrace );
	}

	@Override public boolean isClosed()
	{
		assert mustBeReadableAssertion();
		return closed;
	}

	@Override public void close()
	{
		assert mustBeReadableAssertion();
		assert !closed;
		assert mustBeWritableAssertion();
		lifeGuard.close();
		closed = true;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
