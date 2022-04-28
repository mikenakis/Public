package mikenakis.lifetime;

import mikenakis.kit.coherence.AbstractCoherent;
import mikenakis.kit.coherence.Coherence;
import mikenakis.lifetime.guard.LifeGuard;

/**
 * Abstract base class for lifetime-aware objects.
 */
public abstract class AbstractMortalCoherent extends AbstractCoherent implements Mortal.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );

	protected AbstractMortalCoherent( Coherence coherence )
	{
		super( coherence );
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert mustBeReadableAssertion();
		return lifeGuard.mustBeAliveAssertion();
	}

	@Override public final void close()
	{
		assert mustBeAliveAssertion();
		assert mustBeWritableAssertion();
		onClose();
		lifeGuard.close();
	}

	@Override protected boolean mustBeReadableAssertion()
	{
		assert lifeGuard.mustBeAliveAssertion();
		return super.mustBeReadableAssertion();
	}

	@Override protected boolean mustBeWritableAssertion()
	{
		assert lifeGuard.mustBeAliveAssertion();
		return super.mustBeWritableAssertion();
	}

	protected void onClose()
	{
	}

	@Override public String toString()
	{
		return lifeGuard.toString();
	}
}
