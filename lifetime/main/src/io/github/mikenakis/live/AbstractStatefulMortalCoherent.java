package io.github.mikenakis.live;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.live.guard.LifeGuard;
import io.github.mikenakis.live.guard.StatefulLifeGuard;

/**
 * Abstract base class for lifetime-aware objects that are capable of reporting whether they have been closed.
 */
public abstract class AbstractStatefulMortalCoherent extends AbstractMortalCoherent implements StatefulMortalCoherent.Defaults
{
	private final StatefulLifeGuard lifeGuard = StatefulLifeGuard.of( this );
	@Override protected final LifeGuard lifeGuard() { return lifeGuard; }

	protected AbstractStatefulMortalCoherent( Coherence coherence )
	{
		super( coherence );
	}

	@Override public boolean isClosed()
	{
		return lifeGuard.isClosed();
	}
}
