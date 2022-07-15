package io.github.mikenakis.live;

import io.github.mikenakis.coherence.AbstractCoherent;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.live.guard.LifeGuard;

/**
 * Abstract base class for lifetime-aware objects.
 */
public abstract class AbstractMortalCoherent extends AbstractCoherent implements Mortal.Defaults
{
	protected AbstractMortalCoherent( Coherence coherence )
	{
		super( coherence );
	}

	protected abstract LifeGuard lifeGuard();

	@Override public final void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		onClose();
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard().mustBeAliveAssertion();
	}

	@Override protected boolean mustBeReadableAssertion()
	{
		assert lifeGuard().mustBeAliveAssertion();
		return super.mustBeReadableAssertion();
	}

	@Override protected boolean mustBeWritableAssertion()
	{
		assert lifeGuard().mustBeAliveAssertion();
		return super.mustBeWritableAssertion();
	}

	protected void onClose()
	{
		lifeGuard().close();
	}

	@Override public String toString()
	{
		return lifeGuard().toString();
	}
}
