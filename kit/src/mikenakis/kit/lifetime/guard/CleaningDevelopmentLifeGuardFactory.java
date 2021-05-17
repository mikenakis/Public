package mikenakis.kit.lifetime.guard;

import mikenakis.kit.lifetime.Closeable;

import java.lang.ref.Cleaner;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuardFactory} which makes use of the 'Cleaner' class of Java 9
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class CleaningDevelopmentLifeGuardFactory extends DevelopmentLifeGuardFactory
{
	public static final DevelopmentLifeGuardFactory instance = new CleaningDevelopmentLifeGuardFactory();

	private static final Cleaner cleaner = Cleaner.create(); //this must be static, or else the tests will suffer a huge performance penalty! See https://stackoverflow.com/q/46697432/773113

	public CleaningDevelopmentLifeGuardFactory() //constructor is public to allow creation by tests.
	{
	}

	private final class Guard implements LifeGuard.Defaults
	{
		private final Class<? extends Closeable> closeableClass;
		private final Optional<StackWalker.StackFrame[]> stackTrace;
		private boolean closed;
		private final Cleaner.Cleanable cleanable;

		Guard( Closeable closeable, Optional<StackWalker.StackFrame[]> stackTrace, boolean initialState )
		{
			assert closeable != null;
			closed = !initialState;
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

		@Override public void open()
		{
			assert closed;
			closed = false;
		}

		@Override public boolean lifeStateAssertion( boolean value )
		{
			assert value != closed;
			return true;
		}

		private void clean()
		{
			getFinalizationHandler().invoke( closeableClass, closed, stackTrace );
		}

		@Override public String toString()
		{
			return closed ? "closed" : "open";
		}
	}

	@Override public LifeGuard onNewDevelopmentLifeGuard( Closeable closeable, boolean initiallyAlive, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		return new Guard( closeable, stackTrace, initiallyAlive );
	}
}
