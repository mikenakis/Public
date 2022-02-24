package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;

import java.util.Optional;

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
		return of( 1, closeable, false );
	}

	static LifeGuard of( Closeable closeable, boolean collectStackTrace )
	{
		return of( 1, closeable, collectStackTrace );
	}

	static LifeGuard of( int framesToSkip, Closeable closeable, boolean collectStackTrace )
	{
		if( !Kit.areAssertionsEnabled() )
			return ProductionLifeGuard.instance;
		Optional<StackWalker.StackFrame[]> stackTrace = collectStackTrace( framesToSkip + 1, collectStackTrace );
		if( Kit.get( true ) )
			return CleaningDevelopmentLifeGuard.of( closeable, stackTrace );
		else
			return FinalizingDevelopmentLifeGuard.of( closeable, stackTrace );
	}

	private static Optional<StackWalker.StackFrame[]> collectStackTrace( int framesToSkip, boolean collectStackTrace )
	{
		if( !collectStackTrace )
			return Optional.empty();
		return Optional.of( Kit.getStackTrace( framesToSkip + 1 ) );
	}

	interface Defaults extends LifeGuard, Closeable.Defaults
	{
	}
}
