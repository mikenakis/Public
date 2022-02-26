package mikenakis.kit.mutation;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.StatefulLifeGuard;

public class FreezableMutationContext implements MutationContext, Closeable.Defaults
{
	public static FreezableMutationContext of()
	{
		MutationContext mutationContext = SingleThreadedMutationContext.instance();
		return of( mutationContext );
	}

	public static FreezableMutationContext of( MutationContext parentMutationContext )
	{
		return new FreezableMutationContext( parentMutationContext );
	}

	private final StatefulLifeGuard lifeGuard;
	private final MutationContext parentMutationContext;

	private FreezableMutationContext( MutationContext parentMutationContext )
	{
		this.parentMutationContext = parentMutationContext;
		lifeGuard = StatefulLifeGuard.of( parentMutationContext, this );
	}

	@Override public boolean isInContextAssertion()
	{
		assert parentMutationContext.isInContextAssertion();
		return true;
	}

	@Override public boolean isFrozen()
	{
		return lifeGuard.isClosed();
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert isInContextAssertion();
		lifeGuard.close();
	}
}
