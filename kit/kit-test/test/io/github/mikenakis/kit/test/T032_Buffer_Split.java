package io.github.mikenakis.kit.test;

import io.github.mikenakis.buffer.Buffer;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.List;

/**
 * Test.
 */
public class T032_Buffer_Split
{
	public T032_Buffer_Split()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void Happy_Path_Works()
	{
		Buffer buffer = Buffer.of( "a=b" );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.of( "b" ) ) );
	}

	@Test
	public void Happy_Path_With_Whitespace_Works()
	{
		Buffer buffer = Buffer.of( " a = b " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( " a " ), Buffer.of( " b " ) ) );
	}

	@Test
	public void Happy_Path_With_Whitespace_And_Trim_Works()
	{
		Buffer buffer = Buffer.of( " a = b " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.of( "b" ) ) );
	}

	@Test
	public void Happy_Path_With_Long_Delimiter_Works()
	{
		Buffer buffer = Buffer.of( " a == b " );
		Buffer delimiter = Buffer.of( "==" );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.of( "b" ) ) );
	}

	@Test
	public void Null_Delimiter_Fails()
	{
		Buffer buffer = Buffer.of( "a" );
		Buffer nullBuffer = Kit.get( null );
		TestKit.expect( NullPointerException.class, () -> buffer.split( nullBuffer, false ) );
	}

	@Test
	public void Empty_Delimiter_Fails()
	{
		Buffer buffer = Buffer.of( " a = b " );
		Buffer delimiter = Buffer.EMPTY;
		TestKit.expect( IllegalArgumentException.class, () -> buffer.split( delimiter, false ) );
	}

	@Test
	public void Whitespace_Delimiter_Works()
	{
		Buffer buffer = Buffer.of( "a b" );
		Buffer delimiter = Buffer.of( " " );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.of( "b" ) ) );
	}

	@Test
	public void Whitespace_Delimiter_With_Trim_Works()
	{
		Buffer buffer = Buffer.of( " a  b " );
		Buffer delimiter = Buffer.of( ' ' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.of( "b" ), Buffer.EMPTY ) );
	}

	@Test
	public void Empty_Works()
	{
		Buffer buffer = Buffer.EMPTY;
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.isEmpty();
	}

	@Test
	public void Delimiter_Only_Works()
	{
		Buffer buffer = Buffer.of( "=" );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.EMPTY, Buffer.EMPTY ) );
	}

	@Test
	public void Delimiter_And_Whitespace_Only_Works()
	{
		Buffer buffer = Buffer.of( " = " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.EMPTY, Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Delimiter_With_Whitespace_Works()
	{
		Buffer buffer = Buffer.of( " ab " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( " ab " ) ) );
	}

	@Test
	public void Missing_Delimiter_With_Whitespace_And_Trim_Works()
	{
		Buffer buffer = Buffer.of( " ab " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "ab" ) ) );
	}

	@Test
	public void Missing_Second_Part_Works()
	{
		Buffer buffer = Buffer.of( " a =" );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( " a " ), Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Second_Part_With_Whitespace_And_Trim_Works()
	{
		Buffer buffer = Buffer.of( " a = " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Second_Part_With_Trim_Works()
	{
		Buffer buffer = Buffer.of( " a =" );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.of( "a" ), Buffer.EMPTY ) );
	}

	@Test
	public void Missing_First_Part_Works()
	{
		Buffer buffer = Buffer.of( "= a " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, false ) );
		assert parts.equals( List.of( Buffer.of( "" ), Buffer.of( " a " ) ) );
	}

	@Test
	public void Missing_First_Part_With_Whitespace_And_Trim_Works()
	{
		Buffer buffer = Buffer.of( " = a " );
		Buffer delimiter = Buffer.of( '=' );
		var parts = List.of( buffer.split( delimiter, true ) );
		assert parts.equals( List.of( Buffer.EMPTY, Buffer.of( "a" ) ) );
	}
}
