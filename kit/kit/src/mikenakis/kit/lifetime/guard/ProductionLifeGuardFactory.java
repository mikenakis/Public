package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;

/**
 * Production {@link LifeGuardFactory}.
 *
 * @author michael.gr
 */
public final class ProductionLifeGuardFactory implements LifeGuardFactory.Defaults
{
	public static final LifeGuardFactory instance = new ProductionLifeGuardFactory();

	private ProductionLifeGuardFactory()
	{
	}

	private final LifeGuard dummyLifeGuardInstance = new LifeGuard.Defaults()
	{
		@Override public boolean lifeStateAssertion( boolean value )
		{
			throw new RuntimeException(); //we do not expect this to ever be invoked in production.
		}

		@Override public void close()
		{
			/* nothing to do */
		}
	};

	@Override public LifeGuard newLifeGuard( int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		return dummyLifeGuardInstance;
	}
}
