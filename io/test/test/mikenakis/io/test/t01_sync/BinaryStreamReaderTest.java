package mikenakis.io.test.t01_sync;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure1;
import mikenakis.io.sync.binary.stream.reading.CloseableBinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.testkit.TestKit;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Test.
 *
 * @author michael.gr
 */
public abstract class BinaryStreamReaderTest
{
	static final String BUFFER_FILLER = "0123456789";

	private final byte[] buffer = BUFFER_FILLER.getBytes( StandardCharsets.UTF_8 );

	protected BinaryStreamReaderTest()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	protected abstract CloseableBinaryStreamReader newReader( String content );

	private void withReader( String content, Procedure1<BinaryStreamReader> procedure )
	{
		Kit.tryWith( newReader( content ), procedure );
	}

	@Test
	public void Reading_Zero_Bytes_From_Empty_Stream_Throws_And_Leaves_Buffer_Unchanged()
	{
		withReader( "", reader ->
		{
			TestKit.expect( Kit.bytes.NonPositiveCountException.class, () -> reader.readBuffer( buffer, 1, 0 ) );
			assertBytesEqualString( buffer, BUFFER_FILLER );
		} );
	}

	@Test
	public void Reading_Zero_Bytes_From_Non_Empty_Stream_Throws_And_Leaves_Buffer_Unchanged()
	{
		withReader( "a", reader ->
		{
			TestKit.expect( Kit.bytes.NonPositiveCountException.class, () -> reader.readBuffer( buffer, 1, 0 ) );
			assertBytesEqualString( buffer, BUFFER_FILLER );
		} );
	}

	@Test
	public void Reading_From_Empty_Stream_Yields_Minus_One_And_Leaves_Buffer_Unchanged()
	{
		withReader( "", reader ->
		{
			int r = reader.readBuffer( buffer, 1, 8 );
			assert r == -1; //XXX MINUS ONE
			assertBytesEqualString( buffer, BUFFER_FILLER );
		} );
	}

	@Test
	public void Reading_From_Single_Byte_Stream_Yields_One_Byte_In_Buffer()
	{
		withReader( "a", reader ->
		{
			int r = reader.readBuffer( buffer, 1, 8 );
			assert r == 1;
			assertBytesEqualString( buffer, "0a23456789" );
		} );
	}

	private static void assertBytesEqualString( byte[] bytes, String s )
	{
		String t = new String( bytes, StandardCharsets.UTF_8 );
		assert t.equals( s ) : t + " " + s;
	}
}
