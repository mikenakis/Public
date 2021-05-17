package mikenakis.saganaki.slf4j;

import mikenakis.kit.logging.Log;

/**
 * Implements the Slf4j logger interface by delegating to the mikenakis-kit logger.
 */
public final class KitSlf4jLogger extends BaseSlf4jLogger
{
	KitSlf4jLogger( String name )
	{
		super( name );
	}

	private static String writeThrowable( Throwable t )
	{
		StringBuilder stream = new StringBuilder();
		stream.append( t.getClass().getName() );
		if( t.getMessage() != null )
		{
			stream.append( ": " );
			stream.append( t.getMessage() );
		}
		stream.append( "\n" );
		while( t != null )
		{
			for( StackTraceElement e : t.getStackTrace() )
			{
				stream.append( "    at " );
				stream.append( e.getClassName() ).append( "." ).append( e.getMethodName() );
				stream.append( " (" ).append( getLocation( e ) ).append( ")" );
				stream.append( "\n" );
			}

			t = t.getCause();
			if( t != null )
			{
				stream.append( "Caused by: " ).append( t.getClass().getName() );
				if( t.getMessage() != null )
				{
					stream.append( ": " );
					stream.append( t.getMessage() );
				}
				stream.append( "\n" );
			}
		}
		return stream.toString();
	}

	private static Log.Level logLevelFromSlf4j( int slf4jLevel )
	{
		if( slf4jLevel >= LOG_LEVEL_ERROR )
			return Log.Level.ERROR;
		if( slf4jLevel >= LOG_LEVEL_WARN )
			return Log.Level.WARN;
		if( slf4jLevel >= LOG_LEVEL_INFO )
			return Log.Level.INFO;
		if( slf4jLevel >= LOG_LEVEL_DEBUG )
			return Log.Level.DEBUG;
		assert slf4jLevel >= LOG_LEVEL_TRACE;
		return Log.Level.TRACE;
	}

	@Override protected void log( int numberOfFramesToSkip, int slf4jLevel, String message, Throwable t )
	{
		if( !isLevelEnabled( slf4jLevel ) )
			return;
		Log.Level logLevel = logLevelFromSlf4j( slf4jLevel );
		Log.message( logLevel, numberOfFramesToSkip + 1, getName() + ": " + message );
		if( t != null )
			Log.message( logLevel, numberOfFramesToSkip + 1, writeThrowable( t ) );
	}

	@Override protected boolean isLevelEnabled( int slf4jLevel )
	{
		Log.Level logLevel = logLevelFromSlf4j( slf4jLevel );
		if( logLevel == Log.Level.TRACE )
			return Log.isTracingEnabled;
		return true;
	}

	private static String getLocation( StackTraceElement e )
	{
		assert e != null;
		if( e.isNativeMethod() )
			return "Native Method";
		if( e.getFileName() == null )
			return "Unknown Source";
		if( e.getLineNumber() >= 0 )
			return String.format( "%s:%s", e.getFileName(), e.getLineNumber() );
		return e.getFileName();
	}
}
