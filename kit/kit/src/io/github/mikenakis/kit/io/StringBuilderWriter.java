package io.github.mikenakis.kit.io;

import java.io.Writer;

/**
 * A non-brain-damaged {@link java.io.StringWriter} which uses a {@link StringBuilder} instead of a {@link StringBuffer} and accepts the {@link StringBuilder}
 * as a parameter instead of creating it internally.
 *
 * @author michael.gr
 */
public class StringBuilderWriter extends Writer
{
	private final StringBuilder stringBuilder;

	/**
	 * Constructor.
	 */
	public StringBuilderWriter( StringBuilder stringBuilder )
	{
		this.stringBuilder = stringBuilder;
		//noinspection AssignmentToSuperclassField
		lock = stringBuilder;
	}

	@Override public void write( int c )
	{
		stringBuilder.append( (char)c );
	}

	@Override public void write( char[] characters, int off, int len )
	{
		if( (off < 0) || (off > characters.length) || (len < 0) || ((off + len) > characters.length) || ((off + len) < 0) )
			throw new IndexOutOfBoundsException();
		if( len == 0 )
			return;
		stringBuilder.append( characters, off, len );
	}

	@Override public void write( String str )
	{
		assert str != null;
		stringBuilder.append( str );
	}

	@Override public void write( String str, int off, int len )
	{
		assert str != null;
		stringBuilder.append( str, off, off + len );
	}

	@Override public String toString()
	{
		return stringBuilder.toString();
	}

	@Override public void flush()
	{
	}

	@Override public void close()
	{
	}
}
