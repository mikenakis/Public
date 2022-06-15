package io.github.mikenakis.lifetime;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.lifetime.guard.LifeGuard;
import io.github.mikenakis.lifetime.guard.StatefulLifeGuard;

/**
 * Abstract base class for lifetime-aware objects.
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
