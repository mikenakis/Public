package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;

/**
 * Object lifetime guard (for classes implementing {@link Closeable}).
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
 *
 * @author michael.gr
 */
public interface LifeGuard extends Closeable
{
	static LifeGuard of( Closeable closeable )
	{
		return factory().newLifeGuard( closeable, false );
	}

	static LifeGuard of( Closeable closeable, boolean collectStackTrace )
	{
		return factory().newLifeGuard( closeable, collectStackTrace );
	}

	static LifeGuardFactory factory()
	{
		return Kit.areAssertionsEnabled() ? CleaningDevelopmentLifeGuardFactory.instance : ProductionLifeGuardFactory.instance;
	}

	interface Defaults extends LifeGuard, Closeable.Defaults
	{
	}
}
