package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Mortal;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;

/**
 * A {@link LifeGuard} which contains an explicit open/closed state.
 *
 * @author michael.gr
 */
public final class StatefulLifeGuard extends AbstractCoherent implements Mortal.Defaults
{
	public static StatefulLifeGuard of( Coherence coherence, Mortal mortal )
	{
		return of( coherence, 1, mortal, false );
	}

	public static StatefulLifeGuard of( Coherence coherence, Mortal mortal, boolean collectStackTrace )
	{
		return of( coherence, 1, mortal, collectStackTrace );
	}

	public static StatefulLifeGuard of( Coherence coherence, int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		return new StatefulLifeGuard( coherence, framesToSkip + 1, mortal, collectStackTrace );
	}

	private final LifeGuard lifeGuard;
	private boolean closed;

	private StatefulLifeGuard( Coherence coherence, int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		super( coherence );
		lifeGuard = LifeGuard.of( framesToSkip + 1, mortal, collectStackTrace );
	}

	public boolean isClosed()
	{
		assert mustBeReadableAssertion();
		return closed;
	}

	public boolean isOpen()
	{
		assert mustBeReadableAssertion();
		return !closed;
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
