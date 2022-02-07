package mikenakis.io.sync.binary.stream.reading.helpers;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.buffer.Buffer;

import java.util.Optional;

/**
 * Buffering {@link BinaryStreamReader} with the ability to read up until a delimiter.
 *
 * @author michael.gr
 */
public final class BufferingBinaryStreamReader extends Mutable implements CloseableBinaryStreamReader.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final BinaryStreamReader reader;
	private final Procedure0 onClose;
	private final FillableBuffer fillableBuffer;
	private boolean endHasBeenReached = false;

	public BufferingBinaryStreamReader( MutationContext mutationContext, byte[] cacheBuffer, BinaryStreamReader reader, Procedure0 onClose )
	{
		super( mutationContext );
		assert reader != null;
		assert onClose != null;
		fillableBuffer = new FillableBuffer( mutationContext, cacheBuffer );
		this.reader = reader;
		this.onClose = onClose;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert inContextAssertion();
		assert isAliveAssertion();
		lifeGuard.close();
		onClose.invoke();
	}

	public Optional<Buffer> tryReadUntilDelimiter( Buffer delimiter )
	{
		assert inContextAssertion();
		for( ; ; )
		{
			Optional<Buffer> result = fillableBuffer.readUntilDelimiter( delimiter, endHasBeenReached );
			if( result.isPresent() )
				return result;
			if( load() )
				continue;
			if( fillableBuffer.pack() && load() )
				continue;
			break;
		}
		return Optional.empty();
	}

	@Override public int readBuffer( byte[] bytes, int offset, int count )
	{
		assert inContextAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		if( endHasBeenReached )
			return -1; //XXX MINUS ONE
		int count2 = 0;
		for( ; ; )
		{
			int chunkLength = fillableBuffer.read( bytes, offset + count2, count - count2 );
			assert chunkLength >= 0;
			count2 += chunkLength;
			if( count2 >= count )
				break;
			if( fillableBuffer.getLength() == 0 )
				if( !load() )
					break;
		}
		// assert count2 > 0; //XXX MINUS ONE
		return count2;
	}

	@Override public boolean isFinished()
	{
		return endHasBeenReached;
	}

	private boolean load()
	{
		if( endHasBeenReached )
			return false;
		int freeLength = fillableBuffer.getFreeLength();
		if( freeLength <= 0 )
			return false;
		int r = reader.readBuffer( fillableBuffer.getBytes(), fillableBuffer.getFreeOffset(), freeLength );
		// assert r >= 0; //XXX MINUS ONE
		if( r <= 0 )
		{
			endHasBeenReached = true;
			return true;
		}
		fillableBuffer.fill( r );
		return true;
	}
}
