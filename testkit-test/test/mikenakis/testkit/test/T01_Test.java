package mikenakis.testkit.test;

import mikenakis.io.sync.text.reading.CloseableTextStreamReaderOnBinaryStreamReader;
import mikenakis.io.sync.text.reading.CloseableMemoryTextStreamReader;
import mikenakis.io.sync.text.writing.CloseableTextStreamWriterOnBinaryStreamWriter;
import mikenakis.io.sync.text.writing.CloseableMemoryTextStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;
import mikenakis.testkit.CloseableBinaryStreamReaderFromHex;
import mikenakis.testkit.CloseableBinaryStreamWriterIntoHex;
import org.junit.Test;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T01_Test
{
	private final MutationContext mutationContext = SingleThreadedMutationContext.instance();
	private final BufferAllocator bufferAllocator = BufferAllocator.of( mutationContext, 65536 );

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
		Kit.tryWith( CloseableMemoryTextStreamWriter.create( mutationContext, stringBuilder, Procedure0.noOp ), memoryTextStreamWriter ->
			Kit.tryWith( new CloseableBinaryStreamWriterIntoHex( memoryTextStreamWriter, 16, Procedure0.noOp ), binaryStreamWriter ->
				Kit.tryWith( new CloseableTextStreamWriterOnBinaryStreamWriter( mutationContext, binaryStreamWriter, Procedure0.noOp ), textStreamWriter ->
					textStreamWriter.write( text ) ) ) );
		return stringBuilder.toString();
	}

	private String textFromHex( String hexString )
	{
		return Kit.tryGetWith( CloseableMemoryTextStreamReader.create( mutationContext, bufferAllocator, hexString, Procedure0.noOp ), memoryTextStreamReader ->
			Kit.tryGetWith( new CloseableBinaryStreamReaderFromHex( memoryTextStreamReader, Procedure0.noOp ), binaryStreamReader ->
				Kit.tryGetWith( new CloseableTextStreamReaderOnBinaryStreamReader( mutationContext, bufferAllocator, binaryStreamReader, Procedure0.noOp ), textStreamReader ->
				{
					String text = textStreamReader.tryReadLine().orElseThrow();
					assert textStreamReader.tryReadLine().isEmpty();
					return text;
				} ) ) );
	}

}
