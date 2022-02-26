package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link LifeGuard} which contains an explicit open/closed state.
 *
 * @author michael.gr
 */
public final class StatefulLifeGuard extends Mutable implements Closeable.Defaults
{
	public static StatefulLifeGuard of( MutationContext mutationContext, Closeable closeable )
	{
		return new StatefulLifeGuard( mutationContext, 1, closeable, false );
	}

	public static StatefulLifeGuard of( MutationContext mutationContext, int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		return new StatefulLifeGuard( mutationContext, framesToSkip + 1, closeable, collectStackTrace );
	}

	private final LifeGuard lifeGuard;
	private boolean closed;

	private StatefulLifeGuard( MutationContext mutationContext, int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		super( mutationContext );
		lifeGuard = LifeGuard.of( framesToSkip + 1, closeable, collectStackTrace );
	}

	public boolean isClosed()
	{
		return closed;
	}

	public boolean isOpen()
	{
		return !closed;
	}

	@Override public void close()
	{
		assert canMutateAssertion();
		assert !closed;
		lifeGuard.close();
		closed = true;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
