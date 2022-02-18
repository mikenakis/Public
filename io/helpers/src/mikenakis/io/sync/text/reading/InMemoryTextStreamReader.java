package mikenakis.io.sync.text.reading;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.io.sync.binary.stream.reading.helpers.InMemoryBinaryStreamReader;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.io.sync.text.writing.TextStreamWriter;
import mikenakis.kit.buffer.Buffer;

/**
 * A {@link TextStreamWriter} which reads text from a supplied {@link String}.
 *
 * @author michael.gr
 */
public final class InMemoryTextStreamReader
{
	public static CloseableWrapper<TextStreamReader> of( MutationContext mutationContext, BufferAllocator bufferAllocator, String string, Procedure0 onClose )
	{
		assert onClose != null;
		Buffer buffer = Buffer.of( string );
		CloseableWrapper<BinaryStreamReader> binaryStreamReaderCloseableWrapper = InMemoryBinaryStreamReader.of( mutationContext, buffer, Procedure0.noOp );
		return TextStreamReaderOnBinaryStreamReader.of( mutationContext, bufferAllocator, binaryStreamReaderCloseableWrapper.getTarget(), () -> { binaryStreamReaderCloseableWrapper.close(); onClose.invoke(); } );
	}
}
