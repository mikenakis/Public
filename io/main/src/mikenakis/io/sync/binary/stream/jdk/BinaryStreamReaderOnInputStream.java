package mikenakis.io.sync.binary.stream.jdk;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

import java.io.InputStream;

/**
 * {@link BinaryStreamReader} on Java {@link InputStream}.
 *
 * @author michael.gr
 */
final class BinaryStreamReaderOnInputStream extends Mutable implements CloseableWrapper<BinaryStreamReader>, BinaryStreamReader.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final InputStream inputStream;
	private final Procedure0 onClose;
	private byte[] preview;

	BinaryStreamReaderOnInputStream( MutationContext mutationContext, InputStream inputStream, Procedure0 onClose )
	{
		super( mutationContext );
		assert onClose != null;
		this.inputStream = inputStream;
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

	@Override public int readBuffer( byte[] bytes, int offset, int count )
	{
		assert canMutateAssertion();
		assert isAliveAssertion();
		assert Kit.bytes.validArgumentsAssertion( bytes, offset, count );
		if( preview != null && preview.length > 0 )
		{
			bytes[offset] = preview[0];
			preview = null;
			if( count == 1 )
				return 1;
			return 1 + readBuffer( bytes, offset + 1, count - 1 );
		}
		return Kit.unchecked( () -> inputStream.read( bytes, offset, count ) );
	}

	@Override public boolean isFinished()
	{
		if( preview != null )
			return preview.length == 0;
		preview = new byte[1];
		int n = Kit.unchecked( () -> inputStream.read( preview, 0, 1 ) );
		assert n == -1 || n == 1;
		if( n == -1 )
		{
			preview = new byte[0];
			return true;
		}
		return false;
	}

	@Override public BinaryStreamReader getTarget()
	{
		return this;
	}
}
