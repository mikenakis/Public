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
	static LifeGuard create( Closeable closeable )
	{
		return factory().newLifeGuard( closeable );
	}

	static LifeGuardFactory factory()
	{
		return Kit.areAssertionsEnabled() ? CleaningDevelopmentLifeGuardFactory.instance : ProductionLifeGuardFactory.instance;
	}

	void open();

	interface Defaults extends LifeGuard, Closeable.Defaults
	{
	}
}