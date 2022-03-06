package mikenakis.io.async.binary.stream.writing.helpers;

import mikenakis.io.sync.binary.stream.writing.helpers.BufferStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.Queue;
import mikenakis.tyraki.mutable.MutableCollections;

public class BufferStreamWriterOnAsyncBufferStreamWriter extends Mutable implements BufferStreamWriter, Closeable.Defaults
{
	public static BufferStreamWriterOnAsyncBufferStreamWriter of( MutationContext mutationContext, AsyncBufferStreamWriter asyncBufferStreamWriter, //
		Procedure1<Throwable> errorHandler )
	{
		return new BufferStreamWriterOnAsyncBufferStreamWriter( mutationContext, asyncBufferStreamWriter, errorHandler );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsyncBufferStreamWriter asyncBufferStreamWriter;
	private final Procedure1<Throwable> errorHandler;
	private final Queue<Buffer> queue = MutableCollections.of( mutationContext ).newLinkedQueue();

	private BufferStreamWriterOnAsyncBufferStreamWriter( MutationContext mutationContext, AsyncBufferStreamWriter asyncBufferStreamWriter, //
		Procedure1<Throwable> errorHandler )
	{
		super( mutationContext );
		this.asyncBufferStreamWriter = asyncBufferStreamWriter;
		this.errorHandler = errorHandler;
	}

	@Override public boolean isAliveAssertion()
	{
		assert canReadAssertion();
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		lifeGuard.close();
	}

	@Override public void writeBuffer( Buffer buffer )
	{
		assert canMutateAssertion();
		Buffer lengthBuffer = bufferFromInt( buffer.size() );
		write0( lengthBuffer );
		write0( buffer );
	}

	private static Buffer bufferFromInt( int value )
	{
		return Buffer.of( Kit.bytes.bytesFromInt( value ) );
	}

	private void write0( Buffer buffer )
	{
		boolean wasEmpty = queue.isEmpty();
		queue.enqueue( buffer );
		if( wasEmpty )
			sendNext();
	}

	private void sendNext()
	{
		Buffer buffer = queue.dequeue();
		send0( buffer );
	}

	private void send0( Buffer buffer )
	{
		asyncBufferStreamWriter.writeBuffer( buffer, this::trySendNext, errorHandler );
	}

	private void trySendNext()
	{
		queue.tryDequeue().ifPresent( buffer -> send0( buffer ) );
	}
}
