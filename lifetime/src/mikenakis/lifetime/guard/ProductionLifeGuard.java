package mikenakis.lifetime.guard;

import mikenakis.coherence.Coherence;
import mikenakis.coherence.NullaryCoherence;

/**
 * Production {@link LifeGuard}.
 *
 * @author michael.gr
 */
final class ProductionLifeGuard implements LifeGuard.Defaults
{
	static final LifeGuard instance = new ProductionLifeGuard();

	private ProductionLifeGuard()
	{
	}

	@Override public boolean mustBeAliveAssertion()
	{
		return true;
	}

	@Override public void close()
	{
		/* nothing to do */
	}

	@Override public Coherence coherence()
	{
		return NullaryCoherence.instance;
	}
}
