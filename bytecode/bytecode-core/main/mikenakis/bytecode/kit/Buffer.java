package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Arrays;
import java.util.Objects;

/**
 * An unmodifiable wrapper for an array of bytes.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Buffer
{
	private static final Buffer EMPTY = new Buffer( Kit.ARRAY_OF_ZERO_BYTES, 0, 0 );

	public static Buffer of( byte[] bytes )
	{
		return of( bytes, 0, bytes.length );
	}

	public static Buffer of( byte[] bytes, int start, int length )
	{
		return length == 0 ? EMPTY : new Buffer( bytes, start, length );
	}

	@SuppressWarnings( { "unused", "FieldNamingConvention" } ) public final Object _debugView = new Object()
	{
		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			var builder = new StringBuilder();
			BufferReader bufferReader = BufferReader.of( Buffer.this );
			while( !bufferReader.isAtEnd() )
			{
				int value = bufferReader.readUnsignedByte();
				builder.append( digit( value >>> 4 ) );
				builder.append( digit( value ) );
				builder.append( ' ' );
			}
			return builder.toString();
		}

		@ExcludeFromJacocoGeneratedReport private static char digit( int value )
		{
			value &= 0x0f;
			if( value < 10 )
				return (char)('0' + value);
			value -= 10;
			return (char)('a' + value);
		}
	};

	private final byte[] bytes;
	private final int offset;
	private final int length;
	private boolean isHashCodeComputed;
	private int hashCode;

	Buffer( byte[] bytes, int offset, int length )
	{
		assert offset >= 0;
		assert offset <= bytes.length;
		assert length >= 0;
		assert offset + length <= bytes.length;
		this.bytes = bytes;
		this.offset = offset;
		this.length = length;
	}

	public int length()
	{
		return length;
	}

	public byte[] getBytes()
	{
		return getBytes( 0, length );
	}

	public byte[] getBytes( int position, int count )
	{
		assert position >= 0;
		assert count >= 0;
		assert position + count <= length : new ReadPastEndException();
		if( count == 0 )
			return Kit.ARRAY_OF_ZERO_BYTES;
		byte[] result = new byte[count];
		System.arraycopy( bytes, offset + position, result, 0, count );
		return result;
	}

	public void copyTo( byte[] targetBytes, int targetPosition )
	{
		System.arraycopy( bytes, offset, targetBytes, targetPosition, length );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return length + " bytes";
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof Buffer )
			return equalsBuffer( (Buffer)other );
		assert false;
		return false;
	}

	public boolean equalsBuffer( Buffer other )
	{
		if( length != other.length )
			return false;
		if( hashCode() != other.hashCode() )
			return false;
		return Arrays.compare( bytes, offset, offset + length, other.bytes, other.offset, other.offset + other.length ) == 0;
	}

	@Override public int hashCode()
	{
		if( !isHashCodeComputed )
		{
			hashCode = Objects.hash( hashCode( bytes, offset, length ), length );
			isHashCodeComputed = true;
		}
		return hashCode;
	}

	private static int hashCode( byte[] bytes, int offset, int length )
	{
		int result = 1;
		for( ; length > 0; length-- )
		{
			byte element = bytes[offset++];
			result = 31 * result + element;
		}
		return result;
	}

	BufferReader newReader()
	{
		return BufferReader.of( bytes, offset, length );
	}
}
