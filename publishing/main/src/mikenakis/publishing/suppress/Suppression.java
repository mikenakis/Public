package mikenakis.publishing.suppress;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

public class Suppression extends Mutable implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Suppressable suppressable;

	public Suppression( MutationContext mutationContext, Suppressable suppressable )
	{
		super( mutationContext );
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
