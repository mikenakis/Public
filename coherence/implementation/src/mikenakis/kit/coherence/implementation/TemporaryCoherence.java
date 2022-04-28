package mikenakis.kit.coherence.implementation;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;
import mikenakis.lifetime.Mortal;
import mikenakis.lifetime.guard.LifeGuard;

public class TemporaryCoherence extends AbstractCoherent implements Coherence, Mortal.Defaults
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
