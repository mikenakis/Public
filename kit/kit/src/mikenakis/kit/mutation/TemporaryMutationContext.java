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
	private final Thread constructionThread;

	private TemporaryMutationContext()
	{
		constructionThread = Thread.currentThread();
	}

	@Override public boolean isInContextAssertion()
	{
		assert isAliveAssertion();
		return true;
	}

	@Override public boolean isFrozen()
	{
		return false;
	}

	@Override public boolean isAliveAssertion()
	{
		assert Thread.currentThread() == constructionThread;
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert Thread.currentThread() == constructionThread;
		lifeGuard.close();
	}
}
