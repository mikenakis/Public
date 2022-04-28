package mikenakis.lifetime.guard;

import mikenakis.lifetime.Mortal;

import java.lang.ref.Cleaner;
import java.util.Optional;

/**
 * A {@link DevelopmentLifeGuard} which makes use of the 'Cleaner' class of Java 9
 *
 * @author michael.gr
 */
public final class CleaningDevelopmentLifeGuard extends DevelopmentLifeGuard
{
	public static LifeGuard of( Mortal mortal, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		return new CleaningDevelopmentLifeGuard( mortal, stackTrace );
	}

	private static final Cleaner cleaner = Cleaner.create(); //this must be static, or else the tests will suffer a huge performance penalty! See https://stackoverflow.com/q/46697432/773113

	private final Class<? extends Mortal> mortalClass;
	private final Optional<StackWalker.StackFrame[]> stackTrace;
	private boolean closed;
	private final Cleaner.Cleanable cleanable;

	private CleaningDevelopmentLifeGuard( Mortal mortal, Optional<StackWalker.StackFrame[]> stackTrace )
	{
		super( mortal.coherence() );
		this.stackTrace = stackTrace;
		mortalClass = mortal.getClass();
		cleanable = cleaner.register( mortal, () -> clean() );
	}

	@Override public void close()
	{
		assert !closed;
		closed = true;
		cleanable.clean();
	}

	@Override public boolean mustBeAliveAssertion()
	{
		assert !closed : new MustBeAliveException( mortalClass );
		return true;
	}

	private void clean()
	{
		if( !closed )
			getLifetimeErrorHandler().invoke( mortalClass, stackTrace );
	}

	@Override public String toString()
	{
		return (closed ? "not " : "") + "alive";
	}
}
