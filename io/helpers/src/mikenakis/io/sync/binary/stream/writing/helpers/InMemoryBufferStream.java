package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.io.async.binary.stream.reading.helpers.AsyncBufferStreamReader;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.Queue;
import mikenakis.tyraki.mutable.MutableCollections;

import java.util.Optional;

/**
 * A {@link BufferStreamWriter} and {@link AsyncBufferStreamReader} in memory.
 *
 * @author michael.gr
 */
public class InMemoryBufferStream extends Mutable
{
	public static InMemoryBufferStream of( MutationContext mutationContext )
	{
		return new InMemoryBufferStream( mutationContext );
	}

	public final BufferStreamWriter writer = new BufferStreamWriter.Defaults()
	{
		@Override public void writeBuffer( Buffer buffer )
		{
			buffers.enqueue( buffer );
		}
	};

	private Procedure1<Optional<Buffer>> pendingReceiver = null;

	public final AsyncBufferStreamReader reader = new AsyncBufferStreamReader.Defaults()
	{
		@Override public boolean isBusy()
		{
			return pendingReceiver != null;
		}
		@Override public void readBuffer( Procedure1<Optional<Buffer>> receiver )
		{
			assert pendingReceiver == null; //busy
			pendingReceiver = receiver;
		}
	};

	private final Queue<Buffer> buffers = MutableCollections.of( mutationContext ).newLinkedQueue();

	private InMemoryBufferStream( MutationContext mutationContext )
	{
		super( mutationContext );
	}

	public void tick()
	{
		assert canMutateAssertion();
		while( pendingReceiver != null && !buffers.isEmpty() )
		{
			Buffer buffer = buffers.dequeue();
			var receiver = pendingReceiver;
			pendingReceiver = null;
			receiver.invoke( Optional.of( buffer ) );
		}
	}

	@Override public String toString()
	{
		return buffers.toString();
	}
}
