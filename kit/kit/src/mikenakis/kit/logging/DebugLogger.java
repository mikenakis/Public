package mikenakis.kit.logging;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Logger.
 *
 * @author michael.gr
 */
public class DebugLogger implements Logger
{
	/**
	 * the number of fields in class {@link Event}.
	 */
	private static final int fieldCount = 6;
	private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern( "HH:mm:ss.SSS" ).withZone( ZoneOffset.UTC );
	private final AdaptivePadder adaptivePadder = new AdaptivePadder( fieldCount, " | " );

	@Override public void logEvent( Event event )
	{
		String result = adaptivePadder.concatenate( //
			event.sourceLocation.stringRepresentation(), //
			event.logLevel.toString(), //
			String.valueOf( event.eventNumber ), //
			timeFormat.format( event.timeStamp ), //
			event.thread.getName(), //
			event.message );
		System.out.println( result );
	}
}
