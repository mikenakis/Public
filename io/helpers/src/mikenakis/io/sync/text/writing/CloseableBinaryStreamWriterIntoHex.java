package mikenakis.io.sync.text.writing;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;
import mikenakis.io.sync.binary.stream.writing.BinaryStreamWriter;
import mikenakis.io.sync.binary.stream.writing.CloseableBinaryStreamWriter;
import mikenakis.kit.lifetime.guard.LifeGuard;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.kit.mutation.SingleThreadedMutationContext;

import java.util.Arrays;

public final class CloseableBinaryStreamWriterIntoHex implements CloseableBinaryStreamWriter.Defaults
{
	public static CloseableBinaryStreamWriter of( TextStreamWriter textStreamWriter, int width, Procedure0 onClose )
	{
		return new CloseableBinaryStreamWriterIntoHex( textStreamWriter, width, onClose );
	}

	public static void tryWith( BinaryStreamWriter binaryStreamWriter, int width, Procedure1<BinaryStreamWriter> delegee )
	{
		MutationContext mutationContext = SingleThreadedMutationContext.instance();
		Kit.tryWith( CloseableTextStreamWriterOnBinaryStreamWriter.of( mutationContext, binaryStreamWriter, Procedure0.noOp ), textStreamWriter ->
			Kit.tryWith( of( textStreamWriter, width, Procedure0.noOp ), delegee ) );
	}

	private final LifeGuard lifeGuard = LifeGuard.of( this );
	private final TextStreamWriter textStreamWriter;
	private final char[] hexField;
	private final char[] charField;
	private final Procedure0 onClose;
	private int position;

	private CloseableBinaryStreamWriterIntoHex( TextStreamWriter textStreamWriter, int width, Procedure0 onClose )
	{
		this.textStreamWriter = textStreamWriter;
		hexField = new char[ width * 3 ];
		charField = new char[ width ];
		this.onClose = onClose;
		clear();
	}

	private void clear()
	{
		Arrays.fill( hexField, ' ' );
		Arrays.fill( charField, ' ' );
		position = 0;
	}

	@Override public void writeBytes( byte[] bytes, int index, int count )
	{
		assert isAliveAssertion();
		while( count > 0 )
		{
			internalWriteByte( bytes[index] );
			index++;
			count--;
		}
	}

	private void internalWriteByte( byte value )
	{
		Kit.bytes.toHex( value, hexField, position * 3 );
		charField[position] = value < 32 ? '.' : (char)value;
		position++;
		if( position >= charField.length )
			flush();
	}

	private void flush()
	{
		if( position > 0 )
		{
			textStreamWriter.writeLine( new String( hexField ) + "  " + new String( charField ) );
			clear();
		}
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		flush();
		onClose.invoke();
		lifeGuard.close();
	}
}
