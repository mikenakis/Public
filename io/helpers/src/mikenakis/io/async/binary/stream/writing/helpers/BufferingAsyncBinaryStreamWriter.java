package mikenakis.io.async.binary.stream.writing.helpers;

import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.logging.Log;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.ref.Ref;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.io.async.binary.stream.writing.AsyncBinaryStreamWriter;
import mikenakis.kit.buffer.Buffer;

/**
 * Buffering {@link AsyncBinaryStreamWriter}.
 *
 * @author michael.gr
 */
public final class BufferingAsyncBinaryStreamWriter extends Mutable implements Closeable.Defaults, AsyncBinaryStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final AsyncBinaryStreamWriter unbufferedWriter;
	private final Procedure0 onClose;
	private final byte[] cachingBuffer;
	private int cacheLength = 0;

	public BufferingAsyncBinaryStreamWriter( MutationContext mutationContext, byte[] cachingBuffer, AsyncBinaryStreamWriter unbufferedWriter, Procedure0 onClose )
	{
		super( mutationContext );
		assert unbufferedWriter != null;
		assert onClose != null;
		this.unbufferedWriter = unbufferedWriter;
		this.onClose = onClose;
		this.cachingBuffer = cachingBuffer;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		flush( Procedure0.noOp, Log::error );
		onClose.invoke();
		lifeGuard.close();
	}

	@Override public boolean isBusy()
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		return unbufferedWriter.isBusy();
	}

	@Override public void write( byte[] buffer, int offset, int length, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		assert offset >= 0;
		assert offset < buffer.length;
		assert length > 0;
		assert offset + length <= buffer.length;
		Ref<Integer> countRef = new Ref<>( 0 );
		write( buffer, offset, length, completionHandler, errorHandler, countRef );
	}

	/**
	 * Flushes the buffer to the underlying {@link AsyncBinaryStreamWriter}.
	 */
	public void flush( Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		int length = cacheLength;
		cacheLength = 0;
		unbufferedWriter.write( cachingBuffer, 0, length, completionHandler, errorHandler );
	}

	/**
	 * Checks whether this {@link BufferingAsyncBinaryStreamWriter} contains unflushed data.
	 *
	 * @return {@code true} if there is unflushed data; {@code false} otherwise.
	 */
	public boolean isDirty()
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		return cacheLength != 0;
	}

	/**
	 * Writes a buffer and flushes.
	 *
	 * @param buffer the text to write.
	 */
	public void writeAndFlush( Buffer buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert inMutationContextAssertion();
		isAliveAssertion();
		write( buffer, () -> flush( completionHandler, errorHandler ), errorHandler );
	}

	private void write( byte[] buffer, int offset, int length, Procedure0 completionHandler, Procedure1<Throwable> errorHandler, Ref<Integer> countRef )
	{
		assert inMutationContextAssertion();
		for( ; ; )
		{
			if( cacheLength >= cachingBuffer.length )
			{
				flush( () -> write( buffer, offset, length, completionHandler, errorHandler, countRef ), errorHandler );
				return;
			}
			int chunkLength = Math.min( length - countRef.value, cachingBuffer.length - cacheLength );
			System.arraycopy( buffer, offset + countRef.value, cachingBuffer, cacheLength, chunkLength );
			cacheLength += chunkLength;
			countRef.value += chunkLength;
			if( countRef.value >= length )
				break;
		}
		completionHandler.invoke();
	}
}
