package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.Kit;
import mikenakis.kit.buffer.BufferBuilder;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link BinaryStreamWriter} which accumulates bytes in a supplied {@link BufferBuilder}.
 *
 * @author michael.gr
 */
public final class InMemoryBinaryStreamWriter extends Mutable implements CloseableWrapper<BinaryStreamWriter>, BinaryStreamWriter.Defaults
{
	public static CloseableWrapper<BinaryStreamWriter> of( MutationContext mutationContext, BufferBuilder bufferBuilder, Procedure0 onClose )
	{
		return new InMemoryBinaryStreamWriter( mutationContext, bufferBuilder, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final Procedure0 onClose;
	private final BufferBuilder bufferBuilder;

	private InMemoryBinaryStreamWriter( MutationContext mutationContext, BufferBuilder bufferBuilder, Procedure0 onClose )
	{
		super( mutationContext );
		assert bufferBuilder != null;
		assert onClose != null;
		this.bufferBuilder = bufferBuilder;
		this.onClose = onClose;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public void writeBytes( byte[] bytes, int index, int count )
	{
		assert isAliveAssertion();
		assert canMutateAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, index, count );
		bufferBuilder.append( bytes, index, count );
	}

	@Override public String toString()
	{
		return bufferBuilder.toString();
	}

	@Override public BinaryStreamWriter getTarget()
	{
		return this;
	}
}
