package mikenakis.kit.mutation;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.lifetime.guard.MustBeAliveException;

public final class ConcreteFreezableCoherence implements FreezableCoherence, Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Coherence parentCoherence;
	private boolean isFrozen;

	ConcreteFreezableCoherence( Coherence parentCoherence )
	{
		this.parentCoherence = parentCoherence;
	}

	@Override public String toString()
	{
		return "parent: " + parentCoherence + "; isFrozen: " + isFrozen();
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
		return Kit.assertion( () -> isFrozen() || parentCoherence.mustBeReadableAssertion(), cause -> new MustBeReadableException( this, cause ) );
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return Kit.assertion( () -> !isFrozen() && parentCoherence.mustBeWritableAssertion(), cause -> new MustBeWritableException( this, cause ) );
	}
}
