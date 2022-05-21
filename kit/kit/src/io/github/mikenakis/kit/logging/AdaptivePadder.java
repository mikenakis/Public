package io.github.mikenakis.kit.logging;

/**
 * Concatenates an array of string "fields" into a single string using a delimiter and some adaptively calculated padding
 * so that the delimiters align across repeated invocations.
 *
 * NOTE: this class is not exactly thread safe, but that is okay because in the event of a race condition the worst thing that can happen
 * is that an output line might (rarely) be misaligned with respect to the previous line.
 *
 * @author michael.gr
 */
public final class AdaptivePadder
{
	private final int[] columnWidths;
	private final String delimiter;

	public AdaptivePadder( int fieldCount, String delimiter )
	{
		this.delimiter = delimiter;
		columnWidths = new int[fieldCount];
	}

	public String concatenate( String... fields )
	{
		assert fields.length == columnWidths.length;
		var builder = new StringBuilder();
		for( int i = 0;  i < columnWidths.length; i++ )
		{
			builder.append( fields[i] );
			if( i + 1 < columnWidths.length )
			{
				columnWidths[i] = Math.max( columnWidths[i], fields[i].length() );
				pad( builder, columnWidths[i] - fields[i].length() + 1 );
				builder.append( delimiter );
			}
		}
		return builder.toString();
	}

	private static void pad( StringBuilder builder, int padding )
	{
		for( char c = ' '; padding > 1; padding-- )
		{
			builder.append( c );
			c = '.';
		}
	}
}
