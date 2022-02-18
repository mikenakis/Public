package mikenakis.io.async.binary.stream.jdk;

import mikenakis.io.async.binary.stream.writing.AsyncBinaryStreamWriter;
import mikenakis.io.async.binary.stream.writing.CloseableAsyncBinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.Coherent;
import mikenakis.kit.debug.Debug;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.guard.LifeGuard;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.Optional;

/**
 * {@link AsyncBinaryStreamWriter} on {@link AsynchronousByteChannel}.
 *
 * @author michael.gr
 */
final class AsyncBinaryStreamWriterOnAsynchronousByteChannel extends Coherent implements CloseableAsyncBinaryStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsynchronousByteChannel asynchronousByteChannel;
	private final boolean handOff;
	private Optional<Procedure0> currentCompletionHandler = Optional.empty();
	private Optional<Procedure1<Throwable>> currentErrorHandler = Optional.empty();
	private int currentLength = 0;
	private int busyCount = 0;

	AsyncBinaryStreamWriterOnAsynchronousByteChannel( Coherence coherence, AsynchronousByteChannel asynchronousByteChannel, boolean handOff )
	{
		super( coherence );
		this.asynchronousByteChannel = asynchronousByteChannel;
		this.handOff = handOff;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert coherenceAssertion();
		cohere( () -> {
			assert isAliveAssertion();
			if( handOff )
				Kit.unchecked( asynchronousByteChannel::close );
			lifeGuard.close();
		} );
	}

	@Override public boolean isBusy()
	{
		return busyCount > 0;
	}

	@Override public void write( byte[] buffer, int offset, int length, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		cohere( () -> {
			assert isAliveAssertion();
			assert offset >= 0;
			assert offset < buffer.length;
			assert offset + length <= buffer.length;
			assert currentCompletionHandler.isEmpty();
			currentCompletionHandler = Optional.of( completionHandler );
			currentErrorHandler = Optional.of( errorHandler );
			currentLength = length;
			busyCount++;
			asynchronousByteChannel.write( ByteBuffer.wrap( buffer, offset, length ), null, jdkCompletionHandler );
		} );
	}

	private final CompletionHandler<Integer, Void> jdkCompletionHandler = new CompletionHandler<>()
	{
		@Override public void completed( Integer result, Void attachment )
		{
			Debug.boundary( () -> //
				cohere( () -> //
				{
					assert currentCompletionHandler.isPresent();
					assert result == currentLength;
					Procedure0 tempCompletionHandler = currentCompletionHandler.orElseThrow();
					currentCompletionHandler = Optional.empty();
					busyCount--;
					tempCompletionHandler.invoke();
					//busyCount--;
				} ) );
		}

		@Override public void failed( Throwable throwable, Void attachment )
		{
			Debug.boundary( () -> //
				cohere( () -> //
				{
					assert isBusy();
					assert currentCompletionHandler.isPresent();
					currentCompletionHandler = Optional.empty();
					busyCount--;
					currentErrorHandler.orElseThrow().invoke( throwable );
					//busyCount--;
				} ) );
		}
	};
}
