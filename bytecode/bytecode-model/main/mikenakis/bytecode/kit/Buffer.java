package mikenakis.bytecode.kit;

import mikenakis.kit.Kit;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

/**
 * An unmodifiable wrapper for an array of bytes.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class Buffer
{
	public static final Buffer EMPTY = new Buffer( Kit.ARRAY_OF_ZERO_BYTES, 0, 0 );

	public static Buffer of( int... arguments )
	{
		int length = arguments.length;
		if( length == 0 )
			return EMPTY;
		byte[] bytes = new byte[length];
		for( int i = 0; i < arguments.length; i++ )
		{
			int argument = arguments[i];
			assert argument >= 0 && argument < 256;
			bytes[i] = (byte)argument;
		}
		return new Buffer( bytes, 0, length );
	}

	public static Buffer of( String s )
	{
		byte[] bytes = s.getBytes( StandardCharsets.UTF_8 );
		return of( bytes );
	}

	public static Buffer of( byte[] bytes )
	{
		return of( bytes, 0, bytes.length );
	}

	public static Buffer of( byte[] bytes, int start, int length )
	{
		if( length == 0 )
			return EMPTY;
		return new Buffer( bytes, start, length );
	}

	@SuppressWarnings( { "unused", "FieldNamingConvention" } ) public final Object _debugView = new Object()
	{
		@Override public String toString()
		{
			var builder = new StringBuilder();
			BufferReader bufferReader = new BufferReader( Buffer.this );
			while( !bufferReader.isAtEnd() )
			{
				int value = bufferReader.readUnsignedByte();
				builder.append( digit( value >>> 4 ) );
				builder.append( digit( value ) );
				builder.append( ' ' );
			}
			return builder.toString();
		}

		private char digit( int value )
		{
			value &= 0x0f;
			if( value < 10 )
				return (char)('0' + value);
			value -= 10;
			return (char)('a' + value);
		}
	};

	final byte[] bytes;
	final int offset;
	final int length;

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

	@Override public String toString()
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
		return Arrays.compare( bytes, offset, offset + length, other.bytes, other.offset, other.offset + other.length ) == 0;
	}

	@Override public int hashCode()
	{
		return Objects.hash( hashCode( bytes, offset, length ), length );
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
}
