package mikenakis.testana.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.SourceLocation;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure1;

import java.util.Locale;

/**
 * Measures and reports the time it took to perform an operation.
 * NOTE: this is just a report of the duration of a single operation, it is not a benchmark!
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class TimeMeasurement
{
	public static void run( String message, String formatString, Procedure1<TimeMeasurement> code )
	{
		TimeMeasurement timeMeasurement = new TimeMeasurement( 1, message, formatString );
		code.invoke( timeMeasurement );
		timeMeasurement.end();
	}

	public static <R> R run( String message, String formatString, Function1<R,TimeMeasurement> code )
	{
		TimeMeasurement timeMeasurement = new TimeMeasurement( 1, message, formatString );
		R result = code.invoke( timeMeasurement );
		timeMeasurement.end();
		return result;
	}

	private final double startTime;
	private final String formatString;
	private Object[] arguments = null;
	private final SourceLocation sourceLocation;

	private TimeMeasurement( int numberOfFramesToSkip, String message, String formatString )
	{
		startTime = Kit.time.timeSeconds();
		this.formatString = formatString;
		sourceLocation = Kit.getSourceLocation( numberOfFramesToSkip + 1 );
		TestanaLog.report( message + " | " + sourceLocation.stringRepresentation() );
	}

	public void setArguments( Object... arguments )
	{
		assert this.arguments == null;
		assert arguments != null;
		this.arguments = arguments;
	}

	private void end()
	{
		double elapsed = Kit.time.timeSeconds() - startTime;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( " -- " );
		assert formatString.contains( "%" ) == (arguments != null);
		stringBuilder.append( String.format( formatString, arguments ) );
		stringBuilder.append( String.format( Locale.ROOT, " in %.0f milliseconds", elapsed * 1e3 ) );
		String message = stringBuilder.toString();
		TestanaLog.report( message + " | " + sourceLocation.stringRepresentation() );
	}
}
