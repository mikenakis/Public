package mikenakis.bytecode.kit;

/**
 * A simple string parser.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringParser
{
	private final String s;
	private int i;
	private final int e;

	public StringParser( String s, int i, int e )
	{
		assert i >= 0;
		assert e >= i;
		assert e <= s.length();
		this.s = s;
		this.i = i;
		this.e = e;
	}

	public char parseNextCharacter()
	{
		assert i < e;
		return s.charAt( i++ );
	}

	public boolean isAtEnd()
	{
		assert i <= e;
		return i == e;
	}

	public String parseUntil( char c )
	{
		int index = s.indexOf( c, i );
		assert index != -1;
		String result = s.substring( i, index );
		i = index + 1;
		return result;
	}
}
