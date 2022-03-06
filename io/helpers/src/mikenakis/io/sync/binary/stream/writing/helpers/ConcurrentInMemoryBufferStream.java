package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.dispatch.Dispatcher;
import mikenakis.dispatch.DispatcherProxy;
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
 * The {@link AsyncBufferStreamReader} must be invoked from the dispatcher thread and calls back into the dispatcher thread.
 * The {@link BufferStreamWriter} must be invoked from the remoteDispatcher thread and calls back into th remoteDispatcher thread.
 *
 * @author michael.gr
 */
public class ConcurrentInMemoryBufferStream
{
	public static ConcurrentInMemoryBufferStream of( Dispatcher dispatcher, DispatcherProxy remoteDispatcherProxy )
	{
		return new ConcurrentInMemoryBufferStream( dispatcher, remoteDispatcherProxy );
	}

	private final Dispatcher dispatcher;
	private final DispatcherProxy remoteDispatcherProxy;
	private final BlockingQueue<Buffer> buffers = new LinkedBlockingQueue<>();

	private final AtomicReference<Procedure1<Optional<Buffer>>> pendingReceiverReference = new AtomicReference<>();

	private final AsyncBufferStreamReader reader = new AsyncBufferStreamReader.Defaults()
	{
		@Override public boolean isBusy()
		{
			assert dispatcher.isInContextAssertion();
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
			dispatcher.proxy().post( () -> tick() );
		}
	};

	private final BufferStreamWriter writer = new BufferStreamWriter.Defaults()
	{
		@Override public void writeBuffer( Buffer buffer )
		{
			buffers.add( buffer );
			//remoteDispatcherProxy.post( () -> tick() );
			//dispatcher.proxy().post( () -> tick() );
			tick();
		}
	};

	private ConcurrentInMemoryBufferStream( Dispatcher dispatcher, DispatcherProxy remoteDispatcherProxy )
	{
		assert dispatcher.isInContextAssertion();
		assert remoteDispatcherProxy.outOfContextAssertion();
		this.dispatcher = dispatcher;
		this.remoteDispatcherProxy = remoteDispatcherProxy;
	}

	public BufferStreamWriter writer()
	{
		//assert dispatcher.proxy().outOfContextAssertion();
		return writer;
	}

	public AsyncBufferStreamReader reader()
	{
		//assert dispatcher.mutationContext().isInContextAssertion();
		return reader;
	}

	private void tick()
	{
		assert dispatcher.isInContextAssertion();
		while( !buffers.isEmpty() )
		{
			var receiver = pendingReceiverReference.getAndSet( null );
			if( receiver == null )
				break;
			Buffer buffer = Kit.unchecked( () -> buffers.take() );
			remoteDispatcherProxy.post( () -> receiver.invoke( Optional.of( buffer ) ) );
		}
	}

	@Override public String toString()
	{
		return buffers.toString();
	}
}
