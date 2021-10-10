package mikenakis.bytecode.kit;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Arrays;

/**
 * A writer of {@link Buffer}.
 *
 * @author michael.gr
 */
public class BufferWriter
{
	private byte[] bytes;
	private int position;

	public BufferWriter()
	{
		bytes = new byte[16];
		position = 0;
	}

	private void ensureAdditionalCapacity( int additionalByteCount )
	{
		int minimumCapacity = position + additionalByteCount;
		if( minimumCapacity > bytes.length )
		{
			int growth = minimumCapacity - bytes.length;
			//note: this "+ 2" exists in the StringBuilder code, but I am not sure why.
			int newLength = bytes.length + Math.max( growth, bytes.length + 2 );
			bytes = Arrays.copyOf( bytes, newLength );
		}
	}

	public byte[] toBytes()
	{
		return Arrays.copyOf( bytes, position );
	}

	public int getPosition()
	{
		return position;
	}

	public void writeZeroBytes( int count )
	{
		for( int end = position + count; position < end; )
			writeUnsignedByte( 0 );
	}

	public void writeUnsignedByte( int value )
	{
		assert Helpers.isUnsignedByte( value );
		ensureAdditionalCapacity( 1 );
		bytes[position++] = (byte)value;
	}

	public void writeSignedByte( int value )
	{
		assert Helpers.isSignedByte( value );
		writeUnsignedByte( value & 0xff );
	}

	public void writeUnsignedShort( int value )
	{
		assert Helpers.isUnsignedShort( value );
		ensureAdditionalCapacity( 2 );
		bytes[position] = (byte)(value >>> 8);
		bytes[position + 1] = (byte)value;
		position += 2;
	}

	public void writeSignedShort( int value )
	{
		assert Helpers.isSignedShort( value );
		writeUnsignedShort( value & 0xffff );
	}

	public void writeInt( int position, int value )
	{
		int oldPosition = this.position;
		this.position = position;
		writeInt( value );
		this.position = oldPosition;
	}

	public void writeInt( int value )
	{
		ensureAdditionalCapacity( 4 );
		bytes[position] = (byte)(value >>> 24);
		bytes[position + 1] = (byte)(value >>> 16);
		bytes[position + 2] = (byte)(value >>> 8);
		bytes[position + 3] = (byte)value;
		position += 4;
	}

	public void writeFloat( float value )
	{
		writeInt( Float.floatToIntBits( value ) );
	}

	public void writeLong( long value )
	{
		ensureAdditionalCapacity( 8 );
		bytes[position] = (byte)(value >>> 56);
		bytes[position + 1] = (byte)(value >>> 48);
		bytes[position + 2] = (byte)(value >>> 40);
		bytes[position + 3] = (byte)(value >>> 32);
		bytes[position + 4] = (byte)(value >>> 24);
		bytes[position + 5] = (byte)(value >>> 16);
		bytes[position + 6] = (byte)(value >>> 8);
		bytes[position + 7] = (byte)value;
		position += 8;
	}

	public void writeDouble( double value )
	{
		writeLong( Double.doubleToLongBits( value ) );
	}

	public void writeBuffer( Buffer value )
	{
		ensureAdditionalCapacity( value.length() );
		value.copyTo( bytes, position );
		position += value.length();
	}

	public void writeBytes( byte[] value )
	{
		writeBytes( value, 0, value.length );
	}

	public void writeBytes( byte[] value, int start, int length )
	{
		ensureAdditionalCapacity( length );
		System.arraycopy( value, start, bytes, position, length );
		position += length;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "Position: " + position;
	}
}
