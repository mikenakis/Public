package mikenakis.kit.mutation;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

public class TemporaryMutationContext implements MutationContext, Closeable.Defaults
{
	public static TemporaryMutationContext of()
	{
		return new TemporaryMutationContext();
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );

	private TemporaryMutationContext()
	{
	}

	@Override public boolean inContextAssertion()
	{
		assert isAliveAssertion();
		return true;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		lifeGuard.close();
	}
}
