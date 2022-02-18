package mikenakis.io.sync.binary.stream.reading.helpers;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link BinaryStreamReader} which reads bytes from a supplied {@link Buffer}.
 *
 * @author michael.gr
 */
public final class InMemoryBinaryStreamReader extends Mutable implements CloseableWrapper<BinaryStreamReader>, BinaryStreamReader.Defaults
{
	public static CloseableWrapper<BinaryStreamReader> of( MutationContext mutationContext, Buffer buffer, Procedure0 onClose )
	{
		return new InMemoryBinaryStreamReader( mutationContext, buffer, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final Buffer buffer;
	private int position;

	private InMemoryBinaryStreamReader( MutationContext mutationContext, Buffer buffer, Procedure0 onClose )
	{
		super( mutationContext );
		assert buffer != null;
		assert onClose != null;
		this.buffer = buffer;
		this.onClose = onClose;
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		assert lifeGuard.lifeStateAssertion( true );
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert inMutationContextAssertion();
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public String toString()
	{
		return buffer.subBuffer( position ).toString();
	}

	@Override public int readBuffer( byte[] bytes, int index, int count )
	{
		assert isAliveAssertion();
		assert inMutationContextAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, index, count );
		int availableLength = buffer.size() - position;
		if( availableLength == 0 )
			return -1; //XXX MINUS ONE
		int length = Math.min( availableLength, count );
		buffer.copyBytes( position, bytes, index, length );
		position += length;
		return length;
	}

	@Override public boolean isFinished()
	{
		assert isAliveAssertion();
		assert inMutationContextAssertion();
		return position >= buffer.size();
	}

	@Override public BinaryStreamReader getTarget()
	{
		return this;
	}
}
