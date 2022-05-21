package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.lifetime.Mortal;
import io.github.mikenakis.lifetime.guard.LifeGuard;

public class TemporaryCoherence extends AbstractCoherent implements Coherence.Defaults, Mortal.Defaults
{
	public static TemporaryCoherence of()
	{
		return of( ThreadLocalCoherence.instance() );
	}

	public static TemporaryCoherence of( Coherence parentCoherence )
	{
		return new TemporaryCoherence( parentCoherence );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );

	private TemporaryCoherence( Coherence parentCoherence )
	{
		super( parentCoherence );
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		lifeGuard.close();
	}

	@Override public boolean mustBeReadableAssertion()
	{
		return Kit.assertion( this::mustBeAliveAssertion, cause -> new MustBeReadableException( this, cause ) );
	}

	@Override public boolean mustBeWritableAssertion()
	{
		return Kit.assertion( this::mustBeAliveAssertion, cause -> new MustBeWritableException( this, cause ) );
	}

	@Override public String toString()
	{
		return lifeGuard.toString() + "; parent: " + super.toString();
	}
}
