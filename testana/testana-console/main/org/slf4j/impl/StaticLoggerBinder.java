package org.slf4j.impl;

import io.github.mikenakis.kit.logging.Log;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.spi.LoggerFactoryBinder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides the ILoggerFactory to Slf4j.
 */
@SuppressWarnings( "unused" ) //This class appears to be unused because it is dynamically discovered by Slf4j at runtime.
public final class StaticLoggerBinder implements LoggerFactoryBinder
{
	private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
	private final ILoggerFactory loggerFactory = new Slf4jLoggerFactory();

	private StaticLoggerBinder()
	{
	}

	/**
	 * PEARL: this method is not part of the LoggerFactoryBinder interface, but it must be there, otherwise Slf4j fails.
	 */
	public static StaticLoggerBinder getSingleton()
	{
		return INSTANCE;
	}

	@Override public ILoggerFactory getLoggerFactory()
	{
		return loggerFactory; //NOTE: The instance returned by this method should always be the same object.
	}

	@Override public String getLoggerFactoryClassStr()
	{
		return loggerFactory.getClass().getName();
	}

	private static class Slf4jLoggerFactory implements ILoggerFactory
	{
		private final Map<String,Logger> loggerMap = new ConcurrentHashMap<>();

		Slf4jLoggerFactory()
		{
		}

		@Override public Logger getLogger( String name )
		{
			return loggerMap.computeIfAbsent( name, s -> new SaganakiSlf4jLogger( s ) );
		}
	}

	private static final class SaganakiSlf4jLogger extends BaseSlf4jLogger
	{
		SaganakiSlf4jLogger( String name )
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
			{
				String throwableMessage = writeThrowable( t );
				Log.message( logLevel, numberOfFramesToSkip + 1, throwableMessage );
			}
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

	private abstract static class BaseSlf4jLogger implements Logger
	{
		static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
		static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
		static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
		static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
		static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;
		private final String name;
		private static final int N = 2;

		BaseSlf4jLogger( String name )
		{
			this.name = name;
		}

		private void formatAndLog( int numberOfFramesToSkip, int level, String format, Object arg1, Object arg2 )
		{
			if( !isLevelEnabled( level ) )
				return;
			FormattingTuple tp = MessageFormatter.format( format, arg1, arg2 );
			log( numberOfFramesToSkip + 1, level, tp.getMessage(), tp.getThrowable() );
		}

		private void formatAndLog( int numberOfFramesToSkip, int level, String format, Object... arguments )
		{
			if( !isLevelEnabled( level ) )
				return;
			FormattingTuple tp = MessageFormatter.arrayFormat( format, arguments );
			log( numberOfFramesToSkip + 1, level, tp.getMessage(), tp.getThrowable() );
		}

		protected abstract boolean isLevelEnabled( int logLevel );
		protected abstract void log( int numberOfFramesToSkip, int level, String message, Throwable t );

		public final void log( LoggingEvent event )
		{
			int levelInt = event.getLevel().toInt();
			if( !isLevelEnabled( levelInt ) )
				return;
			FormattingTuple tp = MessageFormatter.arrayFormat( event.getMessage(), event.getArgumentArray(), event.getThrowable() );
			log( N, levelInt, tp.getMessage(), event.getThrowable() );
		}

		@Override public final boolean isTraceEnabled() { return isLevelEnabled( LOG_LEVEL_TRACE ); }
		@Override public final void trace( String msg ) { log( N, LOG_LEVEL_TRACE, msg, null ); }
		@Override public final void trace( String format, Object param1 ) { formatAndLog( N, LOG_LEVEL_TRACE, format, param1, null ); }
		@Override public final void trace( String format, Object param1, Object param2 ) { formatAndLog( N, LOG_LEVEL_TRACE, format, param1, param2 ); }
		@Override public final void trace( String format, Object... argArray ) { formatAndLog( N, LOG_LEVEL_TRACE, format, argArray ); }
		@Override public final void trace( String msg, Throwable t ) { log( N, LOG_LEVEL_TRACE, msg, t ); }
		@Override public final boolean isDebugEnabled() { return isLevelEnabled( LOG_LEVEL_DEBUG ); }
		@Override public final void debug( String msg ) { log( N, LOG_LEVEL_DEBUG, msg, null ); }
		@Override public final void debug( String format, Object param1 ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, param1, null ); }
		@Override public final void debug( String format, Object param1, Object param2 ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, param1, param2 ); }
		@Override public final void debug( String format, Object... argArray ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, argArray ); }
		@Override public final void debug( String msg, Throwable t ) { log( N, LOG_LEVEL_DEBUG, msg, t ); }
		@Override public final boolean isInfoEnabled() { return isLevelEnabled( LOG_LEVEL_INFO ); }
		@Override public final void info( String msg ) { log( N, LOG_LEVEL_INFO, msg, null ); }
		@Override public final void info( String format, Object arg ) { formatAndLog( N, LOG_LEVEL_INFO, format, arg, null ); }
		@Override public final void info( String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_INFO, format, arg1, arg2 ); }
		@Override public final void info( String format, Object... argArray ) { formatAndLog( N, LOG_LEVEL_INFO, format, argArray ); }
		@Override public final void info( String msg, Throwable t ) { log( N, LOG_LEVEL_INFO, msg, t ); }
		@Override public final boolean isWarnEnabled() { return isLevelEnabled( LOG_LEVEL_WARN ); }
		@Override public final void warn( String msg ) { log( N, LOG_LEVEL_WARN, msg, null ); }
		@Override public final void warn( String format, Object arg ) { formatAndLog( N, LOG_LEVEL_WARN, format, arg, null ); }
		@Override public final void warn( String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_WARN, format, arg1, arg2 ); }
		@Override public final void warn( String format, Object... argArray ) { formatAndLog( N, LOG_LEVEL_WARN, format, argArray ); }
		@Override public final void warn( String msg, Throwable t ) { log( N, LOG_LEVEL_WARN, msg, t ); }
		@Override public final boolean isErrorEnabled() { return isLevelEnabled( LOG_LEVEL_ERROR ); }
		@Override public final void error( String msg ) { log( N, LOG_LEVEL_ERROR, msg, null ); }
		@Override public final void error( String format, Object arg ) { formatAndLog( N, LOG_LEVEL_ERROR, format, arg, null ); }
		@Override public final void error( String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_ERROR, format, arg1, arg2 ); }
		@Override public final void error( String format, Object... argArray ) { formatAndLog( N, LOG_LEVEL_ERROR, format, argArray ); }
		@Override public final void error( String msg, Throwable t ) { log( N, LOG_LEVEL_ERROR, msg, t ); }
		@Override public final boolean isTraceEnabled( Marker marker ) { return isTraceEnabled(); }
		@Override public final void trace( Marker marker, String msg ) { log( N, LOG_LEVEL_TRACE, msg, null ); }
		@Override public final void trace( Marker marker, String format, Object arg ) { formatAndLog( N, LOG_LEVEL_TRACE, format, arg, null ); }
		@Override public final void trace( Marker marker, String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_TRACE, format, arg1, arg2 ); }
		@Override public final void trace( Marker marker, String format, Object... arguments ) { formatAndLog( N, LOG_LEVEL_TRACE, format, arguments ); }
		@Override public final void trace( Marker marker, String msg, Throwable t ) { log( N, LOG_LEVEL_TRACE, msg, t ); }
		@Override public final boolean isDebugEnabled( Marker marker ) { return isDebugEnabled(); }
		@Override public final void debug( Marker marker, String msg ) { log( N, LOG_LEVEL_DEBUG, msg, null ); }
		@Override public final void debug( Marker marker, String format, Object arg ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, arg, null ); }
		@Override public final void debug( Marker marker, String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, arg1, arg2 ); }
		@Override public final void debug( Marker marker, String format, Object... arguments ) { formatAndLog( N, LOG_LEVEL_DEBUG, format, arguments ); }
		@Override public final void debug( Marker marker, String msg, Throwable t ) { log( N, LOG_LEVEL_DEBUG, msg, t ); }
		@Override public final boolean isInfoEnabled( Marker marker ) { return isInfoEnabled(); }
		@Override public final void info( Marker marker, String msg ) { log( N, LOG_LEVEL_INFO, msg, null ); }
		@Override public final void info( Marker marker, String format, Object arg ) { formatAndLog( N, LOG_LEVEL_INFO, format, arg, null ); }
		@Override public final void info( Marker marker, String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_INFO, format, arg1, arg2 ); }
		@Override public final void info( Marker marker, String format, Object... arguments ) { formatAndLog( N, LOG_LEVEL_INFO, format, arguments ); }
		@Override public final void info( Marker marker, String msg, Throwable t ) { log( N, LOG_LEVEL_INFO, msg, t ); }
		@Override public final boolean isWarnEnabled( Marker marker ) { return isWarnEnabled(); }
		@Override public final void warn( Marker marker, String msg ) { log( N, LOG_LEVEL_WARN, msg, null ); }
		@Override public final void warn( Marker marker, String format, Object arg ) { formatAndLog( N, LOG_LEVEL_WARN, format, arg, null ); }
		@Override public final void warn( Marker marker, String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_WARN, format, arg1, arg2 ); }
		@Override public final void warn( Marker marker, String format, Object... arguments ) { formatAndLog( N, LOG_LEVEL_WARN, format, arguments ); }
		@Override public final void warn( Marker marker, String msg, Throwable t ) { log( N, LOG_LEVEL_WARN, msg, t ); }
		@Override public final boolean isErrorEnabled( Marker marker ) { return isErrorEnabled(); }
		@Override public final void error( Marker marker, String msg ) { log( N, LOG_LEVEL_ERROR, msg, null ); }
		@Override public final void error( Marker marker, String format, Object arg ) { formatAndLog( N, LOG_LEVEL_ERROR, format, arg, null ); }
		@Override public final void error( Marker marker, String format, Object arg1, Object arg2 ) { formatAndLog( N, LOG_LEVEL_ERROR, format, arg1, arg2 ); }
		@Override public final void error( Marker marker, String format, Object... arguments ) { formatAndLog( N, LOG_LEVEL_ERROR, format, arguments ); }
		@Override public final void error( Marker marker, String msg, Throwable t ) { log( N, LOG_LEVEL_ERROR, msg, t ); }
		@Override public final String toString() { return getClass().getName() + "(" + name + ")"; }
		@Override public final String getName() { return name; }
	}
}
