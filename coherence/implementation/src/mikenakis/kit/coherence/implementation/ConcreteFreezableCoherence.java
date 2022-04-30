package mikenakis.kit.coherence.implementation;

import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.FreezableCoherence;
import mikenakis.lifetime.AbstractMortalCoherent;
import mikenakis.lifetime.guard.MustBeAliveException;

public final class ConcreteFreezableCoherence extends AbstractMortalCoherent implements FreezableCoherence, ImmutabilitySelfAssessable
{
	public static ConcreteFreezableCoherence create()
	{
		return create( ThreadLocalCoherence.instance() );
	}

	public static ConcreteFreezableCoherence create( Coherence parentCoherence )
	{
		return new ConcreteFreezableCoherence( parentCoherence );
	}

	private boolean isFrozen;

	private ConcreteFreezableCoherence( Coherence parentCoherence )
	{
		super( parentCoherence );
	}

	@Override public String toString()
	{
		return "parent: " + coherence + "; isFrozen: " + isFrozen();
	}

	@Override public boolean isFrozen()
	{
		return isFrozen;
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

	@Override public boolean isImmutable()
	{
		return isFrozen;
	}
}