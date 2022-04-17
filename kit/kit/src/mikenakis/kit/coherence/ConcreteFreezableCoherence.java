package mikenakis.kit.coherence;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.AbstractMortalCoherent;
import mikenakis.kit.lifetime.guard.MustBeAliveException;

public final class ConcreteFreezableCoherence extends AbstractMortalCoherent implements FreezableCoherence
{
	private boolean isFrozen;

	ConcreteFreezableCoherence( Coherence parentCoherence )
	{
		super( parentCoherence );
	}

	@Override public String toString()
	{
		return "parent: " + coherence + "; isFrozen: " + isFrozen();
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
		return Kit.assertion( super::mustBeAliveAssertion, cause -> new MustBeAliveException( getClass(), cause ) );
	}

	@Override protected void onClose()
	{
		isFrozen = true;
		super.onClose();
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return Kit.assertion( () -> isFrozen() || coherence.mustBeReadableAssertion(), cause -> new MustBeReadableException( this, cause ) );
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return Kit.assertion( () -> !isFrozen() && coherence.mustBeWritableAssertion(), cause -> new MustBeWritableException( this, cause ) );
	}
}
