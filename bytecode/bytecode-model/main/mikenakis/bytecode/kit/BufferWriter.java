package mikenakis.bytecode.kit;

import java.util.Arrays;

/**
 * A writer of {@link Buffer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class BufferWriter
{
	private byte[] bytes;
	private int position;

	public BufferWriter()
	{
		bytes = new byte[1024];
		position = 0;
	}

	private void reallocate()
	{
		bytes = Arrays.copyOf( bytes, bytes.length * 2 );
	}

	public byte[] toBytes()
	{
		return Arrays.copyOf( bytes, position );
	}

	public int getPosition()
	{
		return position;
	}

	public void writeUnsignedBytes( int count, int value )
	{
		for( int end = position + count;  position < end;  )
			writeUnsignedByte( value );
	}

	public void writeUnsignedByte( int value )
	{
		assert Helpers.isUnsignedByte( value );
		if( position > bytes.length - 1 )
			reallocate();
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
		if( position > bytes.length - 2 )
			reallocate();
		bytes[position] = (byte)(value >>> 8);
		bytes[position + 1] = (byte)value;
		position += 2;
	}

	public void writeSignedShort( int value )
	{
		assert Helpers.isSignedShort( value );
		writeUnsignedShort( value & 0xffff );
	}

	public void writeInt( int value )
	{
		if( position > bytes.length - 4 )
			reallocate();
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
		if( position > bytes.length - 8 )
			reallocate();
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
		while( position > bytes.length - value.length() )
			reallocate();
		value.copyTo( bytes, position );
		position += value.length();
	}

	public void writeBytes( byte[] value )
	{
		writeBytes( value, 0, value.length );
	}

	public void writeBytes( byte[] value, int start, int length )
	{
		while( position > bytes.length - length )
			reallocate();
		System.arraycopy( value, start, bytes, position, length );
		position += length;
	}

	@Override public String toString()
	{
		return "Position: " + position;
	}
}
