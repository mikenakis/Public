package mikenakis.kit.logging;

import mikenakis.kit.Kit;
import mikenakis.kit.SourceLocation;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple logging facility.
 * <p>
 * @author michael.gr
 */
public final class Log
{
	public enum Level
	{
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR
	}

	public static Logger logger = new DebugLogger();
	private static final AtomicInteger eventNumberSeed = new AtomicInteger( 0 );
	public static boolean isTracingEnabled = false;

	private Log()
	{
	}

	/**
	 * Logs a message.
	 *
	 * @param logLevel             the {@link Level} for the message.
	 * @param numberOfFramesToSkip number of frames to skip before obtaining the stackFrame to report as the source of the message.
	 * @param message              the message to log.
	 */
	public static void message( Level logLevel, int numberOfFramesToSkip, String message )
	{
		SourceLocation sourceLocation = Kit.getSourceLocation( numberOfFramesToSkip + 1 );
		int eventNumber = eventNumberSeed.incrementAndGet();
		Instant timeStamp = Instant.now();
		Thread thread = Thread.currentThread();
		Logger.Event event = new Logger.Event( logLevel, sourceLocation, eventNumber, timeStamp, thread, message );
		logger.logEvent( event );
	}

	/**
	 * Logs a message.
	 *
	 * @param logLevel the {@link Level} for the message.
	 * @param message  the message to log.
	 */
	public static void message( Level logLevel, String message )
	{
		message( logLevel, 1, message );
	}

	/**
	 * Logs a trace message.
	 *
	 * @param message the message to log.
	 */
	public static void trace( String message )
	{
		if( !isTracingEnabled )
			return;
		message( Level.TRACE, 1, message );
	}

	/**
	 * Logs a debug message.
	 *
	 * @param message the message to log.
	 */
	public static void debug( String message )
	{
		message( Level.DEBUG, 1, message );
	}

	/**
	 * Logs an informational message.
	 *
	 * @param message the message to log.
	 */
	public static void info( String message )
	{
		message( Level.INFO, 1, message );
	}

	/**
	 * Logs a warning message.
	 *
	 * @param message the message to log.
	 */
	public static void warning( String message )
	{
		message( Level.WARN, 1, message );
	}

	/**
	 * Logs a warning message about a {@link Throwable}.
	 *
	 * @param throwable the {@link Throwable} to log.
	 */
	public static void warning( Throwable throwable )
	{
		String message = Kit.stringFromThrowable( throwable );
		message( Level.WARN, 1, message );
	}

	/**
	 * Logs an error message.
	 *
	 * @param message the message to log.
	 */
	public static void error( String message )
	{
		message( Level.ERROR, 1, message );
	}

	/**
	 * Logs an error message about a {@link Throwable}.
	 *
	 * @param throwable the {@link Throwable} to log.
	 */
	public static void error( Throwable throwable )
	{
		String message = Kit.stringFromThrowable( throwable );
		message( Level.ERROR, 1, message );
	}

	/**
	 * Logs an error message about a {@link Throwable}.
	 *
	 * @param throwable the {@link Throwable} to log.
	 */
	public static void error( String message, Throwable throwable )
	{
		message = Kit.stringFromThrowable( message, throwable );
		message( Level.ERROR, 1, message );
	}
}
