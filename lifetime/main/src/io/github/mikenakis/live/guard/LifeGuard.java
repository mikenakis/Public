package io.github.mikenakis.live.guard;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.live.Mortal;

/**
 * Object lifetime guard (for classes implementing {@link Mortal}).
 * <p>
 * See <a href="https://blog.michael.gr/2021/01/object-lifetime-awareness.html">https://blog.michael.gr/2021/01/object-lifetime-awareness.html</a>
 *
 * @author michael.gr
 */
public interface LifeGuard extends Mortal
{
	static LifeGuard of( Mortal mortal )
	{
		return of( 1, mortal, false );
	}

	static LifeGuard of( Mortal mortal, boolean collectStackTrace )
	{
		return of( 1, mortal, collectStackTrace );
	}

	static LifeGuard of( int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		if( !Kit.areAssertionsEnabled() )
			return ProductionLifeGuard.instance;
		return CleaningDevelopmentLifeGuard.of( framesToSkip + 1, mortal, collectStackTrace );
	}

	interface Defaults extends LifeGuard, Mortal.Defaults
	{ }
}
