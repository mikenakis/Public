package io.github.mikenakis.lifetime.guard;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.NullaryCoherence;

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
