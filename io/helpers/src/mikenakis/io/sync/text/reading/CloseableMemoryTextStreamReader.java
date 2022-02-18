package mikenakis.io.sync.text.reading;

import mikenakis.io.sync.binary.stream.reading.helpers.CloseableMemoryBinaryStreamReader;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.reading.CloseableBinaryStreamReader;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.io.sync.text.writing.TextStreamWriter;
import mikenakis.kit.buffer.Buffer;

/**
 * A {@link TextStreamWriter} which reads text from a supplied {@link String}.
 *
 * @author michael.gr
 */
public final class CloseableMemoryTextStreamReader
{
	public static CloseableTextStreamReader of( MutationContext mutationContext, BufferAllocator bufferAllocator, String string, Procedure0 onClose )
	{
		assert onClose != null;
		Buffer buffer = Buffer.of( string );
		CloseableBinaryStreamReader binaryStreamReader = CloseableMemoryBinaryStreamReader.of( mutationContext, buffer, Procedure0.noOp );
		return CloseableTextStreamReaderOnBinaryStreamReader.of( mutationContext, bufferAllocator, binaryStreamReader, () -> { binaryStreamReader.close(); onClose.invoke(); } );
	}
}
