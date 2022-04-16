package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.mutation.AbstractCoherent;
import mikenakis.kit.mutation.Coherence;

/**
 * A {@link LifeGuard} which contains an explicit open/closed state.
 *
 * @author michael.gr
 */
public final class StatefulLifeGuard extends AbstractCoherent implements Closeable.Defaults
{
	public static StatefulLifeGuard of( Coherence coherence, Closeable closeable )
	{
		return new StatefulLifeGuard( coherence, 1, closeable, false );
	}

	public static StatefulLifeGuard of( Coherence coherence, Closeable closeable, boolean collectStackTrace )
	{
		return new StatefulLifeGuard( coherence, 1, closeable, collectStackTrace );
	}

	public static StatefulLifeGuard of( Coherence coherence, int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		return new StatefulLifeGuard( coherence, framesToSkip + 1, closeable, collectStackTrace );
	}

	private final LifeGuard lifeGuard;
	private boolean closed;

	private StatefulLifeGuard( Coherence coherence, int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		super( coherence );
		lifeGuard = LifeGuard.of( framesToSkip + 1, closeable, collectStackTrace );
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
