package mikenakis.publishing.suppress;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.AbstractCoherent;
import mikenakis.kit.mutation.Coherence;

public class Suppression extends AbstractCoherent implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Suppressable suppressable;

	public Suppression( Coherence coherence, Suppressable suppressable )
	{
		super( coherence );
		this.suppressable = suppressable;
		suppressable.incrementSuppressionCount();
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		suppressable.decrementSuppressionCount();
		lifeGuard.close();
	}
}
