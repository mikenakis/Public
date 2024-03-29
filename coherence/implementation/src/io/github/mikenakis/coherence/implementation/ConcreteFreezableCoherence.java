package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.bathyscaphe.ImmutabilitySelfAssessable;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.FreezableCoherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.live.AbstractMortalCoherent;
import io.github.mikenakis.live.guard.LifeGuard;
import io.github.mikenakis.live.guard.MustBeAliveException;

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

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	@Override protected LifeGuard lifeGuard() { return lifeGuard; }
	private boolean isFrozen;

	private ConcreteFreezableCoherence( Coherence parentCoherence )
	{
		super( parentCoherence );
	}

	@Override public String toString()
	{
		return "parent: " + coherence() + "; isFrozen: " + isFrozen();
	}

	@Override public boolean isFrozen()
	{
		return isFrozen;
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		assert Kit.assertion( super::mustBeAliveAssertion, cause -> new MustBeAliveException( getClass(), cause ) );
		return true;
	}

	@Override protected void onClose()
	{
		isFrozen = true;
		super.onClose();
	}

	@Override public boolean mustBeReadableAssertion()
	{
		assert Kit.assertion( () -> isFrozen() || coherence().mustBeReadableAssertion(), cause -> new MustBeReadableException( this, cause ) );
		return true;
	}

	@Override public boolean mustBeWritableAssertion()
	{
		assert !isFrozen() && coherence().mustBeWritableAssertion() : new MustBeWritableException( this );
		return true;
	}

	@Override public boolean isImmutable()
	{
		return isFrozen;
	}
}
