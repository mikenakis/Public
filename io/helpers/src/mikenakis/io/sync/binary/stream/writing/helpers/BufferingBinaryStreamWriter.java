package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * Buffering {@link BinaryStreamWriter} with the ability to flush.
 *
 * @author michael.gr
 */
public final class BufferingBinaryStreamWriter extends Mutable implements Closeable.Defaults, BinaryStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final BinaryStreamWriter unbufferedWriter;
	private final Procedure0 onClose;
	private final byte[] cachingBuffer;
	private int cacheLength = 0;

	public BufferingBinaryStreamWriter( MutationContext mutationContext, byte[] cachingBuffer, BinaryStreamWriter unbufferedWriter, Procedure0 onClose )
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
		assert !isDirty();
		assert inMutationContextAssertion();
		assert isAliveAssertion();
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public void writeBytes( byte[] bytes, int offset, int count )
	{
		assert inMutationContextAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		for( int count2 = 0; ; )
		{
			if( cacheLength >= cachingBuffer.length )
				flush();
			int chunkLength = Math.min( count - count2, cachingBuffer.length - cacheLength );
			System.arraycopy( bytes, offset + count2, cachingBuffer, cacheLength, chunkLength );
			cacheLength += chunkLength;
			count2 += chunkLength;
			if( count2 >= count )
				break;
		}
	}

	public void flush()
	{
		assert inMutationContextAssertion();
		unbufferedWriter.writeBytes( cachingBuffer, 0, cacheLength );
		cacheLength = 0;
	}

	public boolean isDirty() //I am not sure this method should exist.
	{
		assert inMutationContextAssertion();
		return cacheLength != 0;
	}
}
