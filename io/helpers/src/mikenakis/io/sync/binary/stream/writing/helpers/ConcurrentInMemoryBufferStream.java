package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.dispatch.EventDriver;
import mikenakis.dispatch.Dispatcher;
import mikenakis.io.async.binary.stream.reading.helpers.AsyncBufferStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure1;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Concurrent {@link BufferStreamWriter} and {@link AsyncBufferStreamReader} in memory.
 * The {@link AsyncBufferStreamReader} must be invoked from the {@link EventDriver} thread and calls back into the {@link EventDriver} thread.
 * The {@link BufferStreamWriter} must be invoked from the {@link Dispatcher} thread and calls back into the {@link Dispatcher} thread.
 *
 * @author michael.gr
 */
public class ConcurrentInMemoryBufferStream
{
	public static ConcurrentInMemoryBufferStream of( EventDriver eventDriver, Dispatcher remoteDispatcher )
	{
		return new ConcurrentInMemoryBufferStream( eventDriver, remoteDispatcher );
	}

	private final EventDriver eventDriver;
	private final Dispatcher remoteDispatcher;
	private final BlockingQueue<Buffer> buffers = new LinkedBlockingQueue<>();

	private final AtomicReference<Procedure1<Optional<Buffer>>> pendingReceiverReference = new AtomicReference<>();

	private final AsyncBufferStreamReader reader = new AsyncBufferStreamReader.Defaults()
	{
		@Override public boolean isBusy()
		{
			assert eventDriver.isInContextAssertion();
			return pendingReceiverReference.get() != null;
		}
		@Override public void readBuffer( Procedure1<Optional<Buffer>> receiver )
		{
			pendingReceiverReference.accumulateAndGet( receiver, (a,b)->//
			{
				assert a == null; //busy
				assert b != null;
				return b;
			} );
			eventDriver.dispatcher().post( () -> tick() );
		}
	};

	private final BufferStreamWriter writer = new BufferStreamWriter.Defaults()
	{
		@Override public void writeBuffer( Buffer buffer )
		{
			buffers.add( buffer );
			//remoteDispatcher.post( () -> tick() );
			//eventDriver.proxy().post( () -> tick() );
			tick();
		}
	};

	private ConcurrentInMemoryBufferStream( EventDriver eventDriver, Dispatcher remoteDispatcher )
	{
		assert eventDriver.isInContextAssertion();
		this.eventDriver = eventDriver;
		this.remoteDispatcher = remoteDispatcher;
	}

	public BufferStreamWriter writer()
	{
		assert eventDriver.mutationContext().inContextAssertion();
		return writer;
	}

	public AsyncBufferStreamReader reader()
	{
		assert eventDriver.mutationContext().inContextAssertion();
		return reader;
	}

	private void tick()
	{
		assert eventDriver.isInContextAssertion();
		while( !buffers.isEmpty() )
		{
			var receiver = pendingReceiverReference.getAndSet( null );
			if( receiver == null )
				break;
			Buffer buffer = Kit.unchecked( () -> buffers.take() );
			remoteDispatcher.post( () -> //
				receiver.invoke( Optional.of( buffer ) ) );
		}
	}

	@Override public String toString()
	{
		return buffers.toString();
	}
}
