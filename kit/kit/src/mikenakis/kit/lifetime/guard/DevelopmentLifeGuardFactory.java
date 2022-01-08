package mikenakis.kit.lifetime.guard;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.Closeable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * Development {@link LifeGuardFactory}.
 *
 * @author michael.gr
 */
public abstract class DevelopmentLifeGuardFactory implements LifeGuardFactory.Defaults
{
	public interface FinalizationHandler
	{
		void invoke( Class<? extends Closeable> closeableClass, boolean x, Optional<StackWalker.StackFrame[]> stackTrace );
	}

	private FinalizationHandler finalizationHandler = ( closeableClass, closed, stackTrace ) -> defaultFinalizationHandler( closeableClass, closed, stackTrace );
	public final Collection<String> investigatedCloseableClassNames = new HashSet<>();

	DevelopmentLifeGuardFactory()
	{
		//addInvestigatedCloseableClassName( "mikenakis.sound.framework.ConcreteSequencer" );
		//addInvestigatedCloseableClassName( "mikenakis.sound.framework.AdsrEnvelopePlayer$1" );
		addInvestigatedCloseableClassName( "mikenakis.modelyn.rows.RowSetOnEnumerable" );
	}

	private void addInvestigatedCloseableClassName( String closeableClassName )
	{
		Kit.collection.add( investigatedCloseableClassNames, closeableClassName );
	}

	private boolean isInvestigatedCloseable( Class<? extends Closeable> closeableClass )
	{
		assert !closeableClass.isInterface();
		return Kit.collection.contains( investigatedCloseableClassNames, closeableClass.getName() );
	}

	FinalizationHandler getFinalizationHandler()
	{
		return finalizationHandler;
	}

	/**
	 * Sets a finalization handler. (For use by testing code.)
	 */
	public void setFinalizationHandler( FinalizationHandler finalizationHandler )
	{
		assert finalizationHandler != null;
		this.finalizationHandler = finalizationHandler;
	}

	private static void defaultFinalizationHandler( Class<?> closeableClass, boolean closed, Optional<StackWalker.StackFrame[]> allocationStackTrace )
	{
		if( closed )
			return;
		String stackTraceText = allocationStackTrace.map( DevelopmentLifeGuardFactory::stackFramesToString ).orElse( "Allocation stack trace not available. (This is not an investigated Closeable.)" );
		System.out.println( "Closeable class not closed: " + closeableClass + "\n" + stackTraceText ); //TODO: use logging.
		Kit.debugging.breakPoint();
	}

	protected abstract LifeGuard onNewDevelopmentLifeGuard( Closeable closeable, boolean initiallyAlive, Optional<StackWalker.StackFrame[]> stackTrace );

	@Override public final LifeGuard newLifeGuard( int framesToSkip, Closeable closeable, boolean initiallyAlive )
	{
		boolean investigate = isInvestigatedCloseable( closeable.getClass() );
		Optional<StackWalker.StackFrame[]> stackTrace = investigate ? Optional.of( Kit.getStackTrace( framesToSkip + 2 ) ) : Optional.empty();
		return onNewDevelopmentLifeGuard( closeable, initiallyAlive, stackTrace );
	}

	private static String stackFramesToString( StackWalker.StackFrame[] stackFrames )
	{
		//NOTE: we should not do anything fancy here, because we may be invoked from within finalize()
		var builder = new StringBuilder();
		for( StackWalker.StackFrame stackFrame : stackFrames )
		{
			builder.append( "\tat " );
			builder.append( stackFrame );
			builder.append( '\n' );
		}
		return builder.toString();
	}

}
