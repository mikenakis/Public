package mikenakis.bytecode.test;

import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.exceptions.IncompleteUtf8Exception;
import mikenakis.bytecode.exceptions.MalformedUtf8Exception;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.Kit;
import org.junit.Test;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T001_Failures
{
	public T001_Failures()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Malformed_Utf8_Is_Caught_1()
	{
		for( int i = 0; i < 4; i++ )
		{
			Buffer buffer = Buffer.of( 65, 0b1000_0000 | (i << 4), 65 );
			MalformedUtf8Exception malformedUtf8Exception = Kit.testing.expectException( MalformedUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
			assert malformedUtf8Exception.position == 1 : malformedUtf8Exception.position;
		}
	}

	@Test public void Malformed_Utf8_Is_Caught_2()
	{
		Buffer buffer = Buffer.of( 65, 0b1100_0000, 0b0111_1111, 65 );
		MalformedUtf8Exception malformedUtf8Exception = Kit.testing.expectException( MalformedUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
		assert malformedUtf8Exception.position == 2 : malformedUtf8Exception.position;
	}

	@Test public void Malformed_Utf8_Is_Caught_3()
	{
		Buffer buffer = Buffer.of( 65, 0b1111_0000, 65 );
		MalformedUtf8Exception malformedUtf8Exception = Kit.testing.expectException( MalformedUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
		assert malformedUtf8Exception.position == 1 : malformedUtf8Exception.position;
	}

	@Test public void Malformed_Utf8_Is_Caught_4()
	{
		Buffer buffer = Buffer.of( 65, 0b1110_0000, 0b1000_0000, 0b0111_1111, 65 );
		MalformedUtf8Exception malformedUtf8Exception = Kit.testing.expectException( MalformedUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
		assert malformedUtf8Exception.position == 3 : malformedUtf8Exception.position;
	}

	@Test public void Incomplete_Utf8_Is_Caught_1()
	{
		Buffer buffer = Buffer.of( 65, 0b1100_0000 );
		IncompleteUtf8Exception incompleteUtf8Exception = Kit.testing.expectException( IncompleteUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
		assert incompleteUtf8Exception.position == 1;
	}

	@Test public void Incomplete_Utf8_Is_Caught_2()
	{
		Buffer buffer = Buffer.of( 65, 0b1110_0000, 0b1000_0000 );
		IncompleteUtf8Exception incompleteUtf8Exception = Kit.testing.expectException( IncompleteUtf8Exception.class, () -> Utf8Constant.stringFromBuffer( buffer ) );
		assert incompleteUtf8Exception.position == 2;
	}

	//TODO: test for mikenakis.bytecode.s1.exceptions.UnknownValueException;
}
