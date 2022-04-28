package mikenakis.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.lifetime.Mortal;

import java.util.Optional;

/**
 * Object lifetime guard (for classes implementing {@link Mortal}).
 * See https://blog.michael.gr/2021/01/object-lifetime-awareness.html
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
		Optional<StackWalker.StackFrame[]> stackTrace = collectStackTrace( framesToSkip + 1, collectStackTrace );
		if( Kit.get( true ) )
			return CleaningDevelopmentLifeGuard.of( mortal, stackTrace );
		else
			return FinalizingDevelopmentLifeGuard.of( mortal, stackTrace );
	}

	private static Optional<StackWalker.StackFrame[]> collectStackTrace( int framesToSkip, boolean collectStackTrace )
	{
		if( !collectStackTrace )
			return Optional.empty();
		return Optional.of( Kit.getStackTrace( framesToSkip + 1 ) );
	}

	interface Defaults extends LifeGuard, Mortal.Defaults
	{
	}
}
