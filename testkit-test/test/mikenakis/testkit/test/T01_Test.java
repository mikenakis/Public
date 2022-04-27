package mikenakis.testkit.test;

import mikenakis.debug.Debug;
import org.junit.Test;

import java.util.Locale;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T01_Test
{
	public T01_Test()
	{
		if( !Debug.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void test()
	{
		String content1 = "Hello, world!";
		String hexString = hexFromText( content1 );
		String content2 = textFromHex( hexString );
		assert content2.equals( content1 );
	}

	private static String hexFromText( String text )
	{
		return hexFromText( text, 16 );
	}

	private static String hexFromText( String text, int width )
	{
		StringBuilder stringBuilder1 = new StringBuilder();
		StringBuilder stringBuilder2 = new StringBuilder();
		char[] chars = text.toCharArray();
		int i;
		for( i = 0; i < chars.length; i++ )
		{
			char c = chars[i];
			String hexString = Integer.toHexString( c ).toUpperCase( Locale.ROOT );
			stringBuilder1.append( hexString );
			stringBuilder1.append( ' ' );
			stringBuilder2.append( c < 32 ? '.' : c );
		}
		for( ;  i < width;  i++ )
		{
			stringBuilder1.append( "   " );
			stringBuilder2.append( " " );
		}
		return stringBuilder1.toString() + "  " + stringBuilder2.toString() + "\n";
	}

	private static String textFromHex( String hexString )
	{
		return textFromHex( hexString, 16 );
	}

	private static String textFromHex( String hexString, int width )
	{
		StringBuilder stringBuilder = new StringBuilder();
		char[] charArray = hexString.toCharArray();
		for( int i = 0; i < width; i++ )
		{
			char c1 = charArray[(i * 3)];
			char c2 = charArray[(i * 3) + 1];
			if( c1 == ' ' && c2 == ' ' )
				break;
			String st = c1 + "" + c2;
			char c = (char)Integer.parseInt( st, 16 );
			stringBuilder.append( c );
		}
		return stringBuilder.toString();
	}
}
