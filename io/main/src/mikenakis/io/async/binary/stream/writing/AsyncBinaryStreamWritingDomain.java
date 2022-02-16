package mikenakis.io.async.binary.stream.writing;

import mikenakis.io.async.Async;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.file.Path;

/**
 * Factory for {@link AsyncBinaryStreamWriter}.
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamWritingDomain
{
	CloseableAsyncBinaryStreamWriter newWriterOnAsynchronousByteChannel( AsynchronousByteChannel asynchronousFileChannel, boolean handOff );

	CloseableAsyncBinaryStreamWriter newWriterOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//	Async copy( AsyncBinaryStreamReader reader, AsyncBinaryStreamWriter writer, Procedure1<Long> completionHandler, Procedure1<Throwable> errorHandler );

	interface Defaults extends AsyncBinaryStreamWritingDomain
	{
		MutationContext mutationContext();
		BufferAllocator getBufferAllocator();

//		@Override default Async copy( AsyncBinaryStreamReader reader, AsyncBinaryStreamWriter writer, Procedure1<Long> completionHandler, Procedure1<Throwable> errorHandler )
//		{
//			AsyncBinaryStreamCopier copier = new AsyncBinaryStreamCopier( mutationContext(), getBufferAllocator(), reader, writer, completionHandler, errorHandler );
//			assert !copier.isBusy();
//			return copier.start();
//		}
	}
}
