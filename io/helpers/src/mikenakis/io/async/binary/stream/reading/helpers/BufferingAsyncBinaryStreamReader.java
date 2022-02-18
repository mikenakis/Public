package mikenakis.io.async.binary.stream.reading.helpers;

import mikenakis.io.async.binary.stream.internal.FillableBuffer;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.coherence.Coherent;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.ref.Ref;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;
import mikenakis.kit.buffer.Buffer;

import java.util.Optional;

/**
 * Buffering {@link AsyncBinaryStreamReader}.
 *
 * @author michael.gr
 */
public final class BufferingAsyncBinaryStreamReader extends Coherent implements CloseableWrapper<AsyncBinaryStreamReader>, AsyncBinaryStreamReader.Defaults
{
	public static BufferingAsyncBinaryStreamReader of( Coherence coherence, byte[] cacheBuffer, AsyncBinaryStreamReader unbufferedReader, Procedure0 onClose )
	{
		return new BufferingAsyncBinaryStreamReader( coherence, cacheBuffer, unbufferedReader, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsyncBinaryStreamReader unbufferedReader;
	private final Procedure0 onClose;
	private final FillableBuffer fillableBuffer;
	private boolean endHasBeenReached;
	private int busyCount = 0;

	private BufferingAsyncBinaryStreamReader( Coherence coherence, byte[] cacheBuffer, AsyncBinaryStreamReader unbufferedReader, Procedure0 onClose )
	{
		super( coherence );
		assert coherenceAssertion();
		assert unbufferedReader != null;
		assert onClose != null;
		this.unbufferedReader = unbufferedReader;
		this.onClose = onClose;
		fillableBuffer = new FillableBuffer( coherence.mutationContext(), cacheBuffer );
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert coherenceAssertion();
		//cohere( () ->
		//{
			assert isAliveAssertion();
			onClose.invoke();
			lifeGuard.close();
		//} );
	}

	@Override public boolean isBusy()
	{
		return cohere( () ->
		{
			assert isAliveAssertion();
			return busyCount > 0 || unbufferedReader.isBusy();
		} );
	}

	@Override public void read( byte[] bytes, int offset, int length, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert offset >= 0;
		assert offset < bytes.length;
		assert length > 0;
		assert offset + length <= bytes.length;
		assert completionHandler != null;
		assert errorHandler != null;
		cohere( () ->
		{
			assert isAliveAssertion();
			assert !endHasBeenReached;
			assert !isBusy();
			busyCount++;
			Ref<Integer> countRef = new Ref<>( 0 );
			read0( bytes, offset, length, countRef, //
				count -> //
				{
					assert isBusy();
					busyCount--;
					completionHandler.invoke( count );
				},
				throwable -> //
				{
					assert isBusy();
					busyCount--;
					errorHandler.invoke( throwable );
				} );
		} );
	}

	public void readUntilDelimiter( Buffer delimiter, Procedure1<Optional<Buffer>> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert !delimiter.isEmpty();
		cohere( () ->
		{
			assert isAliveAssertion();
			assert !isBusy();
			busyCount++;
			readUntilDelimiter0( delimiter,
				count -> //
				{
					assert isBusy();
					busyCount--;
					completionHandler.invoke( count );
				},
				throwable -> //
				{
					assert isBusy();
					busyCount--;
					errorHandler.invoke( throwable );
				} );
		} );
	}

	private void readUntilDelimiter0( Buffer delimiter, Procedure1<Optional<Buffer>> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert isAliveAssertion();
		assert coherenceAssertion();
		assert isBusy();
		Optional<Buffer> result = fillableBuffer.readUntilDelimiter( delimiter, endHasBeenReached );
		if( result.isPresent() )
		{
			completionHandler.invoke( result );
			return;
		}
		Procedure0 loadCompletionHandler = () -> readUntilDelimiter0( delimiter, completionHandler, errorHandler );
		if( load( loadCompletionHandler, errorHandler ) )
			return;
		if( fillableBuffer.pack() && load( loadCompletionHandler, errorHandler ) )
			return;
		assert isBusy();
		completionHandler.invoke( Optional.empty() );
	}

	private void read0( byte[] buffer, int offset, int length, Ref<Integer> countRef, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert isAliveAssertion();
		assert coherenceAssertion();
		assert isBusy();
		//noinspection WhileLoopSpinsOnField
		while( !endHasBeenReached )
		{
			int chunkLength = fillableBuffer.read( buffer, offset + countRef.value, length - countRef.value );
			countRef.value += chunkLength;
			if( countRef.value >= length )
				break;
			if( fillableBuffer.getLength() == 0 )
				if( load( () -> read0( buffer, offset, length, countRef, completionHandler, errorHandler ), errorHandler ) )
					return;
		}
		assert isBusy();
		completionHandler.invoke( countRef.value );
	}

	private boolean load( Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert isAliveAssertion();
		assert coherenceAssertion();
		assert isBusy();
		if( endHasBeenReached )
			return false;
		int freeLength = fillableBuffer.getFreeLength();
		if( freeLength <= 0 )
			return false;
		unbufferedReader.read( fillableBuffer.getBytes(), fillableBuffer.getFreeOffset(), freeLength, r ->
		{
			assert coherenceAssertion();
			assert isBusy();
			if( r <= 0 )
				endHasBeenReached = true;
			else
				fillableBuffer.fill( r );
			completionHandler.invoke();
		}, errorHandler );
		return true;
	}

	@Override public AsyncBinaryStreamReader getTarget()
	{
		return this;
	}
}
