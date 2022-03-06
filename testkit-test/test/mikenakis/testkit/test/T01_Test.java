package mikenakis.testkit.test;

import mikenakis.io.sync.text.reading.HexBinaryStreamReader;
import mikenakis.io.sync.text.reading.InMemoryTextStreamReader;
import mikenakis.io.sync.text.reading.TextStreamReaderOnBinaryStreamReader;
import mikenakis.io.sync.text.writing.HexBinaryStreamWriter;
import mikenakis.io.sync.text.writing.InMemoryTextStreamWriter;
import mikenakis.io.sync.text.writing.TextStreamWriterOnBinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.ThreadLocalMutationContext;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T01_Test
{
	private final MutationContext mutationContext = ThreadLocalMutationContext.instance();

	public T01_Test()
	{
		if( !Kit.areAssertionsEnabled() )
			throw new AssertionError();
	}

	@Test public void test()
	{
		String content1 = "Hello, world!";
		String hexString = hexFromText( content1 );
		String content2 = textFromHex( hexString );
		assert content2.equals( content1 );
	}

	private String hexFromText( String text )
	{
		StringBuilder stringBuilder = new StringBuilder();
		Kit.tryWithWrapper( InMemoryTextStreamWriter.of( mutationContext, stringBuilder, Procedure0.noOp ), memoryTextStreamWriter ->
			Kit.tryWithWrapper( HexBinaryStreamWriter.of( memoryTextStreamWriter, 16, Procedure0.noOp ), binaryStreamWriter ->
				Kit.tryWithWrapper( TextStreamWriterOnBinaryStreamWriter.of( mutationContext, binaryStreamWriter, Procedure0.noOp ), textStreamWriter ->
					textStreamWriter.write( text ) ) ) );
		return stringBuilder.toString();
	}

	private String textFromHex( String hexString )
	{
		return Kit.tryGetWithWrapper( InMemoryTextStreamReader.of( mutationContext, hexString, Procedure0.noOp ), memoryTextStreamReader ->
			Kit.tryGetWithWrapper( HexBinaryStreamReader.of( memoryTextStreamReader, Procedure0.noOp ), binaryStreamReader ->
				Kit.tryGetWithWrapper( TextStreamReaderOnBinaryStreamReader.of( mutationContext, binaryStreamReader, Procedure0.noOp ), textStreamReader ->
				{
					String text = textStreamReader.tryReadLine().orElseThrow();
					assert textStreamReader.tryReadLine().isEmpty();
					return text;
				} ) ) );
	}

}
