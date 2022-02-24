package mikenakis.kit.lifetime.guard;

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

	@Override public boolean isAliveAssertion()
	{
		throw new AssertionError(); //we do not expect this to ever be invoked in production.
	}

	@Override public void close()
	{
		/* nothing to do */
	}
}
