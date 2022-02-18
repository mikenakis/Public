package mikenakis.io.sync.text.reading;

import mikenakis.io.sync.binary.stream.reading.helpers.BufferingBinaryStreamReader;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.buffers.BufferAllocation;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.buffers.BufferKey;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class CloseableTextStreamReaderOnBinaryStreamReader extends Mutable implements CloseableTextStreamReader.Defaults
{
	public static CloseableTextStreamReader of( MutationContext mutationContext, BufferAllocator bufferAllocator, BinaryStreamReader binaryStreamReader, Procedure0 onClose )
	{
		return new CloseableTextStreamReaderOnBinaryStreamReader( mutationContext, bufferAllocator, binaryStreamReader, onClose );
	}

	public static final BufferKey bufferKey = new BufferKey( CloseableTextStreamReaderOnBinaryStreamReader.class.getName() );
	private static final Buffer EndOfLine = Buffer.of( "\n" );

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final BufferingBinaryStreamReader bufferingReader;
	private final BufferAllocation readBufferAllocation;

	private CloseableTextStreamReaderOnBinaryStreamReader( MutationContext mutationContext, BufferAllocator bufferAllocator, BinaryStreamReader binaryStreamReader, Procedure0 onClose )
	{
		super( mutationContext );
		assert binaryStreamReader != null;
		assert onClose != null;
		this.onClose = onClose;
		readBufferAllocation = bufferAllocator.newBufferAllocation( bufferKey );
		bufferingReader = new BufferingBinaryStreamReader( mutationContext, readBufferAllocation.bytes, binaryStreamReader, Procedure0.noOp );
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( true );
		return true;
	}

	@Override public void close()
	{
		bufferingReader.close();
		lifeGuard.close();
		onClose.invoke();
		readBufferAllocation.close();
	}

	@Override public Optional<String> tryRead( int length )
	{
		byte[] buffer = new byte[length];
		int n = bufferingReader.readBuffer( buffer );
		if( n <= 0 )
			return Optional.empty();
		return Optional.of( new String( buffer, 0, n, StandardCharsets.UTF_8 ) );
	}

	@Override public Optional<String> tryReadLine()
	{
		Optional<Buffer> buffer = bufferingReader.tryReadUntilDelimiter( EndOfLine );
		return buffer.map( Buffer::toString );
	}
}
