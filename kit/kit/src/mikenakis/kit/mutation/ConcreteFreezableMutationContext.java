package mikenakis.kit.mutation;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.lifetime.guard.MustBeAliveException;

public final class ConcreteFreezableMutationContext implements FreezableMutationContext, Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final MutationContext parentMutationContext;
	private boolean isFrozen;

	ConcreteFreezableMutationContext( MutationContext parentMutationContext )
	{
		this.parentMutationContext = parentMutationContext;
	}

	@Override public String toString()
	{
		return "parent: " + parentMutationContext + "; isFrozen: " + isFrozen();
	}

	private boolean isFrozen()
	{
		return isFrozen;
	}

	@Override public boolean mustBeFrozenAssertion()
	{
		assert isFrozen() : new MustBeFrozenException( this );
		return true;
	}

	@Override public boolean mustNotBeFrozenAssertion()
	{
		assert !isFrozen() : new MustNotBeFrozenException( this );
		return true;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return Kit.assertion( lifeGuard::mustBeAliveAssertion, cause -> new MustBeAliveException( getClass(), cause ) );
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		lifeGuard.close();
		isFrozen = true;
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return Kit.assertion( () -> isFrozen() || parentMutationContext.mustBeReadableAssertion(), cause -> new MustBeReadableException( this, cause ) );
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return Kit.assertion( () -> !isFrozen() && parentMutationContext.mustBeWritableAssertion(), cause -> new MustBeWritableException( this, cause ) );
	}
}
