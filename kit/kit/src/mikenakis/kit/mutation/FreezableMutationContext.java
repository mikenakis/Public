package mikenakis.kit.mutation;

import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

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

	private final LifeGuard lifeGuard = LifeGuard.of( this, true ); //TODO perhaps replace with StatefulLifeGuard ?
	private final MutationContext parentMutationContext;
	private boolean closed;

	private FreezableMutationContext( MutationContext parentMutationContext )
	{
		this.parentMutationContext = parentMutationContext;
	}

	@Override public boolean isInContextAssertion()
	{
		assert parentMutationContext.isInContextAssertion();
		return true;
	}

	@Override public boolean isFrozen()
	{
		return closed;
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
		closed = true;
	}
}
