package mikenakis.io.java.sync.serial;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamWriter;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.io.OutputStream;

/**
 * {@link BinaryStreamWriter} on Java {@link OutputStream}.
 *
 * @author michael.gr
 */
final class BinaryStreamWriterOnOutputStream extends Mutable implements CloseableBinaryStreamWriter.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final OutputStream outputStream;
	private final Procedure0 onClose;

	BinaryStreamWriterOnOutputStream( MutationContext mutationContext, OutputStream outputStream, Procedure0 onClose )
	{
		super( mutationContext );
		this.outputStream = outputStream;
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

	@Override public void writeBytes( byte[] bytes, int offset, int count )
	{
		assert inContextAssertion();
		assert isAliveAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		Kit.unchecked( () -> outputStream.write( bytes, offset, count ) );
	}

	public void flush()
	{
		assert inContextAssertion();
		assert isAliveAssertion();
		Kit.unchecked( outputStream::flush );
	}
}