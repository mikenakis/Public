package mikenakis.io.sync.text.reading;

import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.io.stream.text.CloseableTextStreamReader;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.io.sync.binary.stream.reading.helpers.BinaryStreamReaderOnBuffer;
import mikenakis.kit.io.stream.text.TextStreamWriter;
import mikenakis.kit.buffer.Buffer;

/**
 * A {@link TextStreamWriter} which reads text from a supplied {@link String}.
 *
 * @author michael.gr
 */
public class CloseableMemoryTextStreamReader
{
	public static CloseableTextStreamReader create( MutationContext mutationContext, BufferAllocator bufferAllocator, String string, Procedure0 onClose )
	{
		assert onClose != null;
		Buffer buffer = Buffer.of( string );
		BinaryStreamReaderOnBuffer binaryStreamReader = new BinaryStreamReaderOnBuffer( mutationContext, buffer );
		return new CloseableTextStreamReaderOnBinaryStreamReader( mutationContext, bufferAllocator, binaryStreamReader, () -> { binaryStreamReader.close(); onClose.invoke(); } );
	}
}
