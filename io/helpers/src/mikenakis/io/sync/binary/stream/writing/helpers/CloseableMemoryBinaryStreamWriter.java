package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.kit.Kit;
import mikenakis.kit.buffer.BufferBuilder;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.writing.CloseableBinaryStreamWriter;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link CloseableBinaryStreamWriter} which accumulates bytes in a supplied {@link BufferBuilder}.
 *
 * @author michael.gr
 */
public class CloseableMemoryBinaryStreamWriter extends Mutable implements CloseableBinaryStreamWriter.Defaults
{
	public static CloseableBinaryStreamWriter create( MutationContext mutationContext, BufferBuilder bufferBuilder, Procedure0 onClose )
	{
		return new CloseableMemoryBinaryStreamWriter( mutationContext, bufferBuilder, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final Procedure0 onClose;
	private final BufferBuilder bufferBuilder;

	private CloseableMemoryBinaryStreamWriter( MutationContext mutationContext, BufferBuilder bufferBuilder, Procedure0 onClose )
	{
		super( mutationContext );
		assert bufferBuilder != null;
		assert onClose != null;
		this.bufferBuilder = bufferBuilder;
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

	@Override public void writeBytes( byte[] bytes, int index, int count )
	{
		assert isAliveAssertion();
		assert inMutationContextAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, index, count );
		bufferBuilder.append( bytes, index, count );
	}

	@Override public String toString()
	{
		return bufferBuilder.toString();
	}
}
