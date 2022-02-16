package mikenakis.io.async.binary.stream.writing.helpers;

import mikenakis.codec.Codecs;
import mikenakis.io.async.binary.stream.writing.AsyncBinaryStreamWriter;
import mikenakis.io.sync.binary.stream.writing.helpers.CloseableMemoryBinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffer.BufferBuilder;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

public class AsyncBufferStreamWriterOnAsyncBinaryStreamWriter extends Mutable implements AsyncBufferStreamWriter, Closeable.Defaults
{
	public static AsyncBufferStreamWriterOnAsyncBinaryStreamWriter of( MutationContext mutationContext, AsyncBinaryStreamWriter binaryStreamWriter )
	{
		return new AsyncBufferStreamWriterOnAsyncBinaryStreamWriter( mutationContext, binaryStreamWriter );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final AsyncBinaryStreamWriter binaryStreamWriter;

	private AsyncBufferStreamWriterOnAsyncBinaryStreamWriter( MutationContext mutationContext, AsyncBinaryStreamWriter binaryStreamWriter )
	{
		super( mutationContext );
		this.binaryStreamWriter = binaryStreamWriter;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		lifeGuard.close();
	}

	@Override public boolean isBusy()
	{
		assert false;
		return false;
	}

	@Override public void writeBuffer( Buffer buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		Buffer lengthBuffer = bufferFromInt( buffer.size() );
		binaryStreamWriter.write( lengthBuffer, () -> //
			binaryStreamWriter.write( buffer, completionHandler, errorHandler ), errorHandler );
	}

	private static Buffer bufferFromInt( int value )
	{
		BufferBuilder bufferBuilder = BufferBuilder.of();
		Kit.tryWith( CloseableMemoryBinaryStreamWriter.create( SingleThreadedMutationContext.instance(), bufferBuilder, Procedure0.noOp ), binaryStreamWriter -> //
			Codecs.integerCodec.instanceIntoBinary( value, binaryStreamWriter ) );
		return bufferBuilder.toBuffer();
	}
}
