package mikenakis.io.sync.binary.stream.reading.helpers;

import mikenakis.kit.Kit;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.mutation.MutationContext;

/**
 * {@link BinaryStreamReader} on an array of bytes.
 *
 * @author michael.gr
 */
public final class BinaryStreamReaderOnBuffer extends Mutable implements CloseableBinaryStreamReader.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final Buffer buffer;
	private int arrayOffset;
	private int arrayLength;

	public BinaryStreamReaderOnBuffer( MutationContext mutationContext, Buffer buffer )
	{
		this( mutationContext, buffer, 0, buffer.size() );
	}

	public BinaryStreamReaderOnBuffer( MutationContext mutationContext, Buffer buffer, int arrayOffset, int arrayLength )
	{
		super( mutationContext );
		this.buffer = buffer;
		this.arrayOffset = arrayOffset;
		this.arrayLength = arrayLength;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert inContextAssertion();
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert inContextAssertion();
		assert isAliveAssertion();
		lifeGuard.close();
	}

	@Override public int readBuffer( byte[] bytes, int offset, int count )
	{
		assert isAliveAssertion();
		assert inContextAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		if( arrayLength == 0 )
			return -1; //XXX MINUS ONE
		int n = Math.min( count, arrayLength );
		assert n >= 0;
		buffer.copyTo( arrayOffset, bytes, offset, n );
		arrayOffset += n;
		arrayLength -= n;
		return n;
	}

	@Override public boolean isFinished()
	{
		assert inContextAssertion();
		return arrayLength == 0;
	}
}
