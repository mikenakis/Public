package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;

/**
 * Production {@link LifeGuardFactory}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ProductionLifeGuardFactory implements LifeGuardFactory.Defaults
{
	public static final LifeGuardFactory instance = new ProductionLifeGuardFactory();

	private final LifeGuard lifeGuard = new LifeGuard.Defaults()
	{
		@Override public boolean lifeStateAssertion( boolean value )
		{
			throw new RuntimeException(); //we do not expect this to ever be invoked on production.
		}

		@Override public void close()
		{
			/* nothing to do */
		}

		@Override public void open()
		{
			/* nothing to do */
		}
	};

	private ProductionLifeGuardFactory()
	{
	}

	@Override public LifeGuard newLifeGuard( int framesToSkip, Closeable closeable, boolean initiallyAlive )
	{
		return lifeGuard;
	}
}
