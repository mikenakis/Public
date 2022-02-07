package mikenakis.io.sync.binary.stream.writing;

import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.Mutable;
import mikenakis.kit.mutation.MutationContext;

/**
 * A {@link CloseableBinaryStreamReader} which reads bytes from a supplied {@link Buffer}.
 *
 * @author michael.gr
 */
public class CloseableMemoryBinaryStreamReader extends Mutable implements CloseableBinaryStreamReader.Defaults
{
	public static CloseableBinaryStreamReader create( MutationContext mutationContext, Buffer buffer, Procedure0 onClose )
	{
		return new CloseableMemoryBinaryStreamReader( mutationContext, buffer, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final Procedure0 onClose;
	private final Buffer buffer;
	private int position;

	private CloseableMemoryBinaryStreamReader( MutationContext mutationContext, Buffer buffer, Procedure0 onClose )
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
		lifeGuard.close();
		onClose.invoke();
	}

	@Override public String toString()
	{
		return buffer.subBuffer( position ).toString();
	}

	@Override public int readBuffer( byte[] bytes, int index, int count )
	{
		int length = Math.min( buffer.size() - position, count );
		buffer.copyBytes( position, bytes, index, length );
		position += length;
		return length;
	}

	@Override public boolean isFinished()
	{
		return position >= buffer.size();
	}
}
