package mikenakis.bytecode.test;

import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.exceptions.IncompleteMutf8Exception;
import mikenakis.bytecode.exceptions.MalformedMutf8Exception;
import mikenakis.bytecode.kit.Buffer;
import mikenakis.kit.Kit;
import org.junit.Test;

/**
 * test.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class T001_Mutf8
{
	public T001_Mutf8()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new RuntimeException( "assertions are not enabled!" );
	}

	@Test public void Plain_Text_Checks_Out()
	{
		test( "the quick brown fox jumps over the lazy dog" );
		test( "LOREM IPSUM DOLOR SIT AMET" );
	}

	@Test public void Fancy_Text_Checks_Out()
	{
		test( "κάνω βουτιές σε βόθρο με εικόνες, φουσκώνω τα βυζιά μου με ορμόνες" );
		test( "θέλω να γίνω σαν Αμερικάνος, μ' αρέσει στα κρυφά κι ο Μητροπάνος" );
		test( "子罕: 子曰：「麻冕，禮也；今也純，儉。吾從眾。拜下，禮也；今拜乎上，泰也。雖違眾，吾從下。」" );
		test( "\u0000\u0001\u0002\u0010\u001f" );
		//noinspection NonAsciiCharacters
		test( "Test\u0000Πενία\u0001Τέχνας\u0002Κατεργάζεται\u0010" );
	}

	private static void test( String s1 )
	{
		Mutf8Constant c1 = Mutf8Constant.of( s1 );
		Buffer buffer = c1.buffer();
		Mutf8Constant c2 = Mutf8Constant.of( buffer );
		String s2 = c2.stringValue();
		assert s1.equals( s2 );
	}

	@Test public void Malformed_Utf8_Is_Caught_1()
	{
		for( int i = 0; i < 4; i++ )
		{
			Buffer buffer = Buffer.of( 65, 0b1000_0000 | (i << 4) );
			MalformedMutf8Exception exception = Kit.testing.expectException( MalformedMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
			assert exception.position == 1 : exception.position;
		}
	}

	@Test public void Malformed_Mutf8_Is_Caught_2()
	{
		Buffer buffer = Buffer.of( 65, 0b1100_0000, 0b0111_1111 );
		MalformedMutf8Exception exception = Kit.testing.expectException( MalformedMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 2 : exception.position;
	}

	@Test public void Malformed_Mutf8_Is_Caught_3()
	{
		Buffer buffer = Buffer.of( 65, 0b1111_0000 );
		MalformedMutf8Exception exception = Kit.testing.expectException( MalformedMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 1 : exception.position;
	}

	@Test public void Malformed_Mutf8_Is_Caught_4()
	{
		Buffer buffer = Buffer.of( 65, 0b1110_0000, 0b1000_0000, 0b0111_1111 );
		MalformedMutf8Exception exception = Kit.testing.expectException( MalformedMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 3 : exception.position;
	}

	@Test public void Malformed_Mutf8_Is_Caught_5()
	{
		Buffer buffer = Buffer.of( 65, 0b1110_0000, 0b1100_0000 );
		MalformedMutf8Exception exception = Kit.testing.expectException( MalformedMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 2 : exception.position;
	}

	@Test public void Incomplete_Mutf8_Is_Caught_1()
	{
		Buffer buffer = Buffer.of( 65, 0b1100_0000 );
		IncompleteMutf8Exception exception = Kit.testing.expectException( IncompleteMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 1;
	}

	@Test public void Incomplete_Mutf8_Is_Caught_2()
	{
		Buffer buffer = Buffer.of( 65, 0b1110_0000, 0b1000_0000 );
		IncompleteMutf8Exception exception = Kit.testing.expectException( IncompleteMutf8Exception.class, () -> Mutf8Constant.of( buffer ).stringValue() );
		assert exception.position == 2;
	}
}
