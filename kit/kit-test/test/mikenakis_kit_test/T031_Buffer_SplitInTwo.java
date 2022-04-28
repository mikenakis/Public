package mikenakis_kit_test;

import mikenakis.buffer.Buffer;
import mikenakis.kit.Dyad;
import mikenakis.kit.Kit;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.util.Optional;

/**
 * Test.
 */
public class T031_Buffer_SplitInTwo
{
	public T031_Buffer_SplitInTwo()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test
	public void Happy_Path_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( "a=b" ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.of( "a" ), Optional.of( Buffer.of( "b" ) ) );
	}

	@Test
	public void Happy_Path_With_Whitespace_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a = b " ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.of( " a " ), Optional.of( Buffer.of( " b " ) ) );
	}

	@Test
	public void Happy_Path_With_Whitespace_And_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a = b " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.of( "a" ), Optional.of( Buffer.of( "b" ) ) );
	}

	@Test
	public void Happy_Path_With_Long_Delimiter_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a == b " ).splitInTwo( Buffer.of( "==" ), true );
		assert dyad.equals( Buffer.of( "a" ), Optional.of( Buffer.of( "b" ) ) );
	}

	@Test
	public void Null_Delimiter_Fails()
	{
		Buffer nullBuffer = Kit.get( null );
		TestKit.expect( NullPointerException.class, () -> Buffer.of( "a" ).splitInTwo( nullBuffer, false ) );
	}

	@Test
	public void Empty_Delimiter_Fails()
	{
		TestKit.expect( AssertionError.class, () -> Buffer.of( " a = b " ).splitInTwo( Buffer.EMPTY, false ) );
	}

	@Test
	public void Whitespace_Delimiter_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( "a b" ).splitInTwo( Buffer.of( " " ), false );
		assert dyad.equals( Buffer.of( "a" ), Optional.ofNullable( Buffer.of( "b" ) ) );
	}

	@Test
	public void Whitespace_Delimiter_With_Trim_Fails()
	{
		TestKit.expect( AssertionError.class, () -> Buffer.of( " " ).splitInTwo( Buffer.of( ' ' ), true ) );
	}

	@Test
	public void Empty_String_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.EMPTY.splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.EMPTY, Optional.empty() );
	}

	@Test
	public void String_Containing_Delimiter_Only_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( "=" ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.EMPTY, Optional.of( Buffer.EMPTY ) );
	}

	@Test
	public void String_Containing_Delimiter_And_Whitespace_Only_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " = " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.EMPTY, Optional.of( Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Delimiter_With_Whitespace_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " ab " ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.of( " ab " ), Optional.empty() );
	}

	@Test
	public void Missing_Delimiter_With_Whitespace_And_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " ab " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.of( "ab" ), Optional.empty() );
	}

	@Test
	public void Missing_Second_Part_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a =" ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.of( " a " ), Optional.of( Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Second_Part_With_Whitespace_And_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a = " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.of( "a" ), Optional.of( Buffer.EMPTY ) );
	}

	@Test
	public void Missing_Second_Part_With_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " a =" ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.of( "a" ), Optional.of( Buffer.EMPTY ) );
	}

	@Test
	public void Missing_First_Part_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( "= a " ).splitInTwo( Buffer.of( '=' ), false );
		assert dyad.equals( Buffer.EMPTY, Optional.ofNullable( Buffer.of( " a " ) ) );
	}

	@Test
	public void Missing_First_Part_With_Whitespace_And_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( " = a " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.EMPTY, Optional.ofNullable( Buffer.of( "a" ) ) );
	}

	@Test
	public void Missing_First_Part_With_Trim_Works()
	{
		Dyad<Buffer,Optional<Buffer>> dyad = Buffer.of( "= a " ).splitInTwo( Buffer.of( '=' ), true );
		assert dyad.equals( Buffer.EMPTY, Optional.ofNullable( Buffer.of( "a" ) ) );
	}
}
