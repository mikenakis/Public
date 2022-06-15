package io.github.mikenakis.publishing.suppress;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.lifetime.AbstractMortalCoherent;
import io.github.mikenakis.lifetime.guard.LifeGuard;

public final class Suppression extends AbstractMortalCoherent
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	@Override protected LifeGuard lifeGuard() { return lifeGuard; }
	private final Suppressable suppressable;

	public Suppression( Coherence coherence, Suppressable suppressable )
	{
		super( coherence );
		this.suppressable = suppressable;
		suppressable.incrementSuppressionCount();
	}

	@Override protected void onClose()
	{
		suppressable.decrementSuppressionCount();
		super.onClose();
	}
}
