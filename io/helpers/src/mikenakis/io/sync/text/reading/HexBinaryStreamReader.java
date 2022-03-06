package mikenakis.io.sync.text.reading;

import mikenakis.io.sync.binary.stream.reading.BinaryStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.lifetime.CloseableWrapper;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.FreezableMutationContext;

import java.util.Optional;

public final class HexBinaryStreamReader implements CloseableWrapper<BinaryStreamReader>, BinaryStreamReader.Defaults
{
	public static void tryWith( BinaryStreamReader binaryStreamReader, Procedure1<BinaryStreamReader> delegee )
	{
		Object result = tryGetWith( binaryStreamReader, hexBinaryStreamReader -> //
		{
			delegee.invoke( hexBinaryStreamReader );
			return null;
		} );
		assert result == null;
	}

	public static <T> T tryGetWith( BinaryStreamReader binaryStreamReader, Function1<T,BinaryStreamReader> delegee )
	{
		return Kit.tryGetWith( FreezableMutationContext.of(), mutationContext -> //
			Kit.tryGetWithWrapper( TextStreamReaderOnBinaryStreamReader.of( mutationContext, binaryStreamReader, Procedure0.noOp ), textStreamReader -> //
				Kit.tryGetWithWrapper( of( textStreamReader, Procedure0.noOp ), delegee ) ) );
	}

	public static CloseableWrapper<BinaryStreamReader> of( TextStreamReader textStreamReader, Procedure0 onClose )
	{
		return new HexBinaryStreamReader( textStreamReader, onClose );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final TextStreamReader textStreamReader;
	private final Procedure0 onClose;
	private byte[] buffer;
	private int position;

	private HexBinaryStreamReader( TextStreamReader textStreamReader, Procedure0 onClose )
	{
		this.textStreamReader = textStreamReader;
		this.onClose = onClose;
		load();
	}

	private void load()
	{
		Optional<String> optionalLine = textStreamReader.tryReadLine();
		if( optionalLine.isEmpty() )
		{
			buffer = null;
			return;
		}
		String line = optionalLine.get();
		int dividerIndex = line.indexOf( "   " );
		assert dividerIndex != -1;
		dividerIndex++;
		assert dividerIndex % 3 == 0;
		int width = dividerIndex / 3;
		buffer = new byte[width];
		for( int i = 0; i < width; i++ )
			buffer[i] = Kit.bytes.fromHex( line, i * 3 );
		position = 0;
	}

	@Override public int readBuffer( byte[] bytes, int index, int count )
	{
		assert Kit.bytes.validArgumentsAssertion( bytes, index, count );
		if( buffer == null )
			return 0;
		int startIndex = index;
		while( count > 0 && !isFinished() )
		{
			bytes[index] = internalReadByte();
			index++;
			count--;
		}
		int result = index - startIndex;
		assert result > 0;
		return result;
	}

	private byte internalReadByte()
	{
		byte result = buffer[position];
		position++;
		if( position >= buffer.length )
			load();
		return result;
	}

	@Override public boolean isFinished()
	{
		return buffer == null;
	}

	@Override public boolean isAliveAssertion()
	{
		assert lifeGuard.isAliveAssertion();
		return true;
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		onClose.invoke();
		lifeGuard.close();
	}

	@Override public BinaryStreamReader getTarget()
	{
		return this;
	}
}
