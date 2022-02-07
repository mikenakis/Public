package mikenakis.testkit;

import mikenakis.io.sync.text.reading.CloseableTextStreamReaderOnBinaryStreamReader;
import mikenakis.kit.Kit;
import mikenakis.kit.buffers.BufferAllocator;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.CloseableBinaryStreamReader;
import mikenakis.kit.io.stream.text.TextStreamReader;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

import java.util.Optional;

public class CloseableBinaryStreamReaderFromHex implements CloseableBinaryStreamReader.Defaults
{
	public static void tryWith( BinaryStreamReader binaryStreamReader, BufferAllocator bufferAllocator, Procedure1<BinaryStreamReader> delegee )
	{
		Object result = tryGetWith( binaryStreamReader, bufferAllocator, hexBinaryStreamReader ->
		{
			delegee.invoke( hexBinaryStreamReader );
			return null;
		} );
		assert result == null;
	}

	public static <T> T tryGetWith( BinaryStreamReader binaryStreamReader, BufferAllocator bufferAllocator, Function1<T,BinaryStreamReader> delegee )
	{
		MutationContext mutationContext = SingleThreadedMutationContext.instance();
		return Kit.tryGetWith( new CloseableTextStreamReaderOnBinaryStreamReader( mutationContext, bufferAllocator, binaryStreamReader, Procedure0.noOp ), textStreamReader ->
			Kit.tryGetWith( new CloseableBinaryStreamReaderFromHex( textStreamReader, Procedure0.noOp ), delegee ) );
	}

	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final TextStreamReader textStreamReader;
	private final Procedure0 onClose;
	private byte[] buffer;
	private int position;

	public CloseableBinaryStreamReaderFromHex( TextStreamReader textStreamReader, Procedure0 onClose )
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
		for( int i = 0;  i < width;  i++ )
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

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		onClose.invoke();
		lifeGuard.close();
	}
}