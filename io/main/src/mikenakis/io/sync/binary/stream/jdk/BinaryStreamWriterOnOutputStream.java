package mikenakis.io.sync.binary.stream.jdk;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.io.OutputStream;

/**
 * {@link BinaryStreamWriter} on Java {@link OutputStream}.
 *
 * @author michael.gr
 */
final class BinaryStreamWriterOnOutputStream extends Mutable implements CloseableWrapper<BinaryStreamWriter>, BinaryStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final OutputStream outputStream;
	private final Procedure0 onClose;

	BinaryStreamWriterOnOutputStream( MutationContext mutationContext, OutputStream outputStream, Procedure0 onClose )
	{
		super( mutationContext );
		this.outputStream = outputStream;
		this.onClose = onClose;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert canMutateAssertion();
		assert isAliveAssertion();
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public void writeBytes( byte[] bytes, int offset, int count )
	{
		assert canMutateAssertion();
		assert isAliveAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		Kit.unchecked( () -> outputStream.write( bytes, offset, count ) );
	}

	public void flush()
	{
		assert canMutateAssertion();
		assert isAliveAssertion();
		Kit.unchecked( outputStream::flush );
	}

	@Override public BinaryStreamWriter getTarget()
	{
		return this;
	}
}
