package mikenakis.kit.mutation;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

/* package-private */ class LocalMutationContext implements MutationContext, Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );

	@Override public boolean inContextAssertion()
	{
		assert isAliveAssertion();
		return true;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		lifeGuard.close();
	}
}
