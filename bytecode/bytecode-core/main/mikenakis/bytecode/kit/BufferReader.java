package mikenakis.bytecode.kit;

/**
 * A reader of {@link Buffer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class BufferReader
{
	public static BufferReader of( Buffer buffer )
	{
		return buffer.newReader();
	}

	public static BufferReader of( byte[] bytes, int offset, int length )
	{
		return new BufferReader( bytes, offset, length );
	}

	private final byte[] bytes;
	private final int offset;
	private int position;
	private final int endPosition;

	private BufferReader( byte[] bytes, int offset, int length )
	{
		this.bytes = bytes;
		this.offset = offset;
		position = offset;
		endPosition = offset + length;
	}

	public int getPosition()
	{
		return position - offset;
	}

	public boolean isAtEnd()
	{
		assert position <= endPosition;
		return position == endPosition;
	}

	public void skip( int count )
	{
		assert position + count <= endPosition : new ReadPastEndException();
		position += count;
	}

	public int readUnsignedByte()
	{
		assert position + 1 <= endPosition : new ReadPastEndException();
		int result = bytes[position] & 255;
		position++;
		return result;
	}

	public int readSignedByte()
	{
		int result = readUnsignedByte();
		return Helpers.signExtendByte( result );
	}

	public int readUnsignedShort()
	{
		assert position + 2 <= endPosition : new ReadPastEndException();
		int result = ((bytes[position] & 255) << 8) | //
			(bytes[position + 1] & 255);
		position += 2;
		return result;
	}

	public int readSignedShort()
	{
		int result = readUnsignedShort();
		return Helpers.signExtendShort( result );
	}

	public int readInt()
	{
		assert position + 4 <= endPosition : new ReadPastEndException();
		int result = (bytes[position] & 255) << 24 | //
			(bytes[position + 1] & 255) << 16 | //
			(bytes[position + 2] & 255) << 8 | //
			bytes[position + 3] & 255;
		position += 4;
		return result;
	}

	public float readFloat()
	{
		return Float.intBitsToFloat( readInt() );
	}

	public long readLong()
	{
		assert position + 8 <= endPosition : new ReadPastEndException();
		long result = ((long)(bytes[position] & 255) << 56) | //
			((long)(bytes[position + 1] & 255) << 48) | //
			((long)(bytes[position + 2] & 255) << 40) | //
			((long)(bytes[position + 3] & 255) << 32) | //
			((long)(bytes[position + 4] & 255) << 24) | //
			((long)(bytes[position + 5] & 255) << 16) | //
			((long)(bytes[position + 6] & 255) << 8) | //
			((long)bytes[position + 7] & 255);
		position += 8;
		return result;
	}

	public double readDouble()
	{
		return Double.longBitsToDouble( readLong() );
	}

	public Buffer readBuffer( int count )
	{
		assert count >= 0;
		assert position + count <= endPosition : new ReadPastEndException();
		Buffer result = new Buffer( bytes, position, count );
		position += count;
		return result;
	}
}
