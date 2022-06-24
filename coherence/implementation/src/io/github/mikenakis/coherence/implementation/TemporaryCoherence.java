package io.github.mikenakis.coherence.implementation;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeReadableException;
import io.github.mikenakis.coherence.implementation.exceptions.MustBeWritableException;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.live.Live;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.live.guard.LifeGuard;

public interface TemporaryCoherence extends Coherent, Coherence.Defaults
{
	static Live<Coherence> of()
	{
		return of( ThreadLocalCoherence.instance() );
	}

	static Live<Coherence> of( Coherence parentCoherence )
	{
		class Implementation extends AbstractCoherent implements Coherence.Defaults, Mortal.Defaults
		{
			private final LifeGuard lifeGuard = LifeGuard.of( this );

			private Implementation( Coherence parentCoherence )
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

		var result = new Implementation( parentCoherence );
		return Live.of( result, result, result::close );
	}
}
