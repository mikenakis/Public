package mikenakis.io.async.binary.stream.jdk;

import mikenakis.kit.Kit;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.Coherent;
import mikenakis.kit.debug.Debug;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.Optional;

/**
 * {@link Closeable} {@link AsyncBinaryStreamReader} on {@link AsynchronousByteChannel}.
 *
 * @author michael.gr
 */
final class AsyncBinaryStreamReaderOnAsynchronousByteChannel extends Coherent implements CloseableWrapper<AsyncBinaryStreamReader>, AsyncBinaryStreamReader.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsynchronousByteChannel asynchronousByteChannel;
	private final boolean handOff;
	private Optional<Procedure1<Integer>> currentCompletionHandler = Optional.empty();
	private Optional<Procedure1<Throwable>> currentErrorHandler = Optional.empty();
	private int busyCount = 0;

	AsyncBinaryStreamReaderOnAsynchronousByteChannel( Coherence coherence, AsynchronousByteChannel asynchronousByteChannel, boolean handOff )
	{
		super( coherence );
		this.asynchronousByteChannel = asynchronousByteChannel;
		this.handOff = handOff;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert inMutationContextAssertion();
		cohere( () -> //
		{
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

	@Override public void read( byte[] bytes, int offset, int length, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert isAliveAssertion();
		//assert !isBusy();
		assert offset >= 0;
		assert offset < bytes.length;
		assert length > 0;
		assert offset + length <= bytes.length;
		cohere( () -> {
			assert isAliveAssertion();
			assert !isBusy();
			assert currentCompletionHandler.isEmpty();
			currentCompletionHandler = Optional.of( completionHandler );
			currentErrorHandler = Optional.of( errorHandler );
			busyCount++;
			asynchronousByteChannel.read( ByteBuffer.wrap( bytes, offset, length ), null, jdkCompletionHandler );
		} );
	}

	private final CompletionHandler<Integer, Void> jdkCompletionHandler = new CompletionHandler<>()
	{
		@Override public void completed( Integer result, Void attachment )
		{
			Debug.boundary( () -> //
				cohere( () -> //
				{
					assert isBusy();
					assert currentCompletionHandler.isPresent();
					Procedure1<Integer> tempCompletionHandler = currentCompletionHandler.get();
					currentCompletionHandler = Optional.empty();
					busyCount--;
					tempCompletionHandler.invoke( result );
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
				} ) );
		}
	};

	@Override public AsyncBinaryStreamReader getTarget()
	{
		return this;
	}
}
