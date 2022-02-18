package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;

/**
 * {@link LifeGuard} Factory.
 *
 * @author michael.gr
 */
public interface LifeGuardFactory
{
	LifeGuard newLifeGuard( int framesToSkip, Closeable closeable, boolean collectStackTrace );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	LifeGuard newLifeGuard( Closeable closeable, boolean collectStackTrace );

	LifeGuard newLifeGuard( Closeable closeable );

	interface Defaults extends LifeGuardFactory
	{
		@Override default LifeGuard newLifeGuard( Closeable closeable, boolean collectStackTrace )
		{
			return newLifeGuard( 1, closeable, collectStackTrace );
		}

		@Override default LifeGuard newLifeGuard( Closeable closeable )
		{
			return newLifeGuard( 1, closeable, false );
		}
	}
}
