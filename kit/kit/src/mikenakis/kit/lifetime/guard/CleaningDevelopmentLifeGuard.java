package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;

import java.lang.ref.Cleaner;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuard} which makes use of the 'Cleaner' class of Java 9
 *
 * @author michael.gr
 */
public final class CleaningDevelopmentLifeGuard extends DevelopmentLifeGuard
{
	public static LifeGuard of( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		return new CleaningDevelopmentLifeGuard( closeable, stackTrace );
	}

	private static final Cleaner cleaner = Cleaner.create(); //this must be static, or else the tests will suffer a huge performance penalty! See https://stackoverflow.com/q/46697432/773113

	private final Class<? extends Closeable> closeableClass;
	private final Optional<StackWalker.StackFrame[]> stackTrace;
	private boolean closed;
	private final Cleaner.Cleanable cleanable;

	private CleaningDevelopmentLifeGuard( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		this.stackTrace = stackTrace;
		closeableClass = closeable.getClass();
		cleanable = cleaner.register( closeable, () -> clean() );
	}

	@Override public void close()
	{
		assert !closed;
		closed = true;
		cleanable.clean();
	}

	@Override public boolean isAliveAssertion()
	{
		assert !closed : new EndOfLifeException( closeableClass );
		return true;
	}

	private void clean()
	{
		if( !closed )
			getLifetimeErrorHandler().invoke( closeableClass, stackTrace );
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
