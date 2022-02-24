package mikenakis.io.async.binary.stream.jdk;

import mikenakis.kit.Kit;
import mikenakis.kit.debug.Debug;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

/**
 * {@link AsynchronousByteChannel} on {@link AsynchronousFileChannel}.
 */
final class AsynchronousByteChannelOnAsynchronousFileChannel extends Mutable implements AsynchronousByteChannel, Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsynchronousFileChannel asynchronousFileChannel;
	private final boolean handOff;
	private long position;

	AsynchronousByteChannelOnAsynchronousFileChannel( MutationContext mutationContext, AsynchronousFileChannel asynchronousFileChannel, boolean handOff, long position )
	{
		super( mutationContext );
		this.asynchronousFileChannel = asynchronousFileChannel;
		this.handOff = handOff;
		this.position = position;
	}

	@Override public boolean isOpen()
	{
		assert false; //we do not have this information. TODO we need a stateful lifeguard.
		return true;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		if( handOff )
			Kit.unchecked( asynchronousFileChannel::close );
		lifeGuard.close();
	}

	@Override public <A> void read( ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler )
	{
		asynchronousFileChannel.read( dst, position, attachment, new CompletionHandler<>()
		{
			@Override public void completed( Integer result, A attachment2 )
			{
				Debug.boundary( () -> //
				{
					if( result > 0 )
						position += result;
					handler.completed( result, attachment2 );
				} );
			}

			@Override public void failed( Throwable exc, A attachment2 )
			{
				Debug.boundary( () -> handler.failed( exc, attachment2 ) );
			}
		} );
	}

	@Override public Future<Integer> read( ByteBuffer dst )
	{
		assert false : new UnsupportedOperationException();
		return null;
	}

	@Override public <A> void write( ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler )
	{
		asynchronousFileChannel.write( src, position, attachment, new CompletionHandler<>()
		{
			@Override public void completed( Integer result, A attachment2 )
			{
				Debug.boundary( () -> //
				{
					position += result;
					handler.completed( result, attachment2 );
				} );
			}

			@Override public void failed( Throwable exc, A attachment2 )
			{
				Debug.boundary( () -> handler.failed( exc, attachment2 ) );
			}
		} );
	}

	@Override public Future<Integer> write( ByteBuffer src )
	{
		assert false : new UnsupportedOperationException();
		return null;
	}
}
