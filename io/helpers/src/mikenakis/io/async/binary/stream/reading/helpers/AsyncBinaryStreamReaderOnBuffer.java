package mikenakis.io.async.binary.stream.reading.helpers;

import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.io.async.binary.stream.reading.CloseableAsyncBinaryStreamReader;
import mikenakis.kit.buffer.Buffer;

/**
 * {@link CloseableAsyncBinaryStreamReader} on {@link Buffer}.
 *
 * @author michael.gr
 */
public final class AsyncBinaryStreamReaderOnBuffer extends Mutable implements CloseableAsyncBinaryStreamReader.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Buffer buffer;
	private int arrayOffset;
	private int arrayLength;
	private int busyCount = 0;

	public AsyncBinaryStreamReaderOnBuffer( MutationContext mutationContext, Buffer buffer )
	{
		this( mutationContext, buffer, 0, buffer.size() );
	}

	public AsyncBinaryStreamReaderOnBuffer( MutationContext mutationContext, Buffer buffer, int arrayOffset, int arrayLength )
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
}
