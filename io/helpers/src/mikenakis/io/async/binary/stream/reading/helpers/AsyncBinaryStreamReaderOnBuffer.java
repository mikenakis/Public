package mikenakis.io.async.binary.stream.reading.helpers;

import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReader;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.buffer.Buffer;

/**
 * {@link AsyncBinaryStreamReader} on {@link Buffer}.
 *
 * @author michael.gr
 */
public final class AsyncBinaryStreamReaderOnBuffer extends Mutable implements CloseableWrapper<AsyncBinaryStreamReader>, AsyncBinaryStreamReader.Defaults
{
	public static AsyncBinaryStreamReaderOnBuffer of( MutationContext mutationContext, Buffer buffer, int arrayOffset, int arrayLength )
	{
		return new AsyncBinaryStreamReaderOnBuffer( mutationContext, buffer, arrayOffset, arrayLength );
	}

	public static AsyncBinaryStreamReaderOnBuffer of( MutationContext mutationContext, Buffer buffer )
	{
		return of( mutationContext, buffer, 0, buffer.size() );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Buffer buffer;
	private int arrayOffset;
	private int arrayLength;
	private int busyCount = 0;

	private AsyncBinaryStreamReaderOnBuffer( MutationContext mutationContext, Buffer buffer, int arrayOffset, int arrayLength )
	{
		super( mutationContext );
		assert inMutationContextAssertion();
		this.buffer = buffer;
		this.arrayOffset = arrayOffset;
		this.arrayLength = arrayLength;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert inMutationContextAssertion();
		assert isAliveAssertion();
		assert !isBusy();
		lifeGuard.close();
	}

	@Override public boolean isBusy()
	{
		assert isAliveAssertion();
		return busyCount > 0;
	}

	@Override public void read( byte[] bytes, int offset, int length, Procedure1<Integer> completionHandler, Procedure1<Throwable> errorHandler )
	{
		assert isAliveAssertion();
		assert !isBusy();
		assert inMutationContextAssertion();
		assert offset >= 0;
		assert offset < bytes.length;
		assert length > 0;
		assert offset + length <= bytes.length;
		//busyCount++;
		if( arrayLength == 0 )
		{
			completionHandler.invoke( -1 );
		}
		else
		{
			busyCount++;
			length = Math.min( length, arrayLength );
			buffer.copyTo( arrayOffset, bytes, offset, length );
			arrayOffset += length;
			arrayLength -= length;
			busyCount--;
			completionHandler.invoke( length );
		}
		//busyCount--;
	}

	@Override public AsyncBinaryStreamReader getTarget()
	{
		return this;
	}
}
