package io.github.mikenakis.live.guard;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.SourceLocation;
import io.github.mikenakis.live.Mortal;

import java.lang.ref.Cleaner;
import java.util.Collection;
import java.util.List;

/**
 * A {@link DevelopmentLifeGuard} which makes use of the 'Cleaner' class of Java 9
 *
 * @author michael.gr
 */
public final class CleaningDevelopmentLifeGuard extends DevelopmentLifeGuard
{
	public static LifeGuard of( int framesToSkip, Mortal mortal, boolean collectStackTrace )
	{
		Collection<SourceLocation> stackTrace = collectStackTrace ? Kit.getAllSourceLocations( framesToSkip + 1 ) : List.of();
		return new CleaningDevelopmentLifeGuard( mortal, stackTrace );
	}

	private static final Cleaner cleaner = Cleaner.create(); //PEARL: this must be static, or there will be a huge performance penalty! See https://stackoverflow.com/q/46697432/773113

	private final Class<? extends Mortal> mortalClass;
	private final Collection<SourceLocation> stackTrace;
	private boolean closed;
	private final Cleaner.Cleanable cleanable;

	private CleaningDevelopmentLifeGuard( @SuppressWarnings( "TypeMayBeWeakened" ) Mortal mortal, Collection<SourceLocation> stackTrace )
	{
		super( mortal.coherence() );
		this.stackTrace = stackTrace;
		mortalClass = mortal.getClass();
		cleanable = cleaner.register( mortal, this::clean );
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
