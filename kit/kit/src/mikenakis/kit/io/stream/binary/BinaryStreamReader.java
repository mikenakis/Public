package mikenakis.kit.io.stream.binary;

import java.util.Arrays;

/**
 * Synchronous Binary Stream Reader.
 *
 * @author michael.gr
 */
public interface BinaryStreamReader
{
	/**
	 * Reads up to a given number of bytes.
	 *
	 * @param bytes the buffer to read into.
	 * @param index the offset within the buffer to start storing bytes.
	 * @param count the maximum number of bytes to read.
	 *
	 * @return the number of bytes read; may be less than {@param length} if the end of the stream is reached.
	 *
	 * @see java.io.InputStream#read(byte[], int, int) for details.
	 */
	int readBuffer( byte[] bytes, int index, int count );

	boolean isFinished();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads up to a given number of bytes and stores them in an array of bytes.
	 *
	 * @param buffer the buffer to read into.
	 *
	 * @return the number of bytes read; may be less than {@param length} if the end of the stream is reached.
	 *
	 * @see java.io.InputStream#read(byte[], int, int) for details.
	 */
	int readBuffer( byte[] buffer );

	/**
	 * Reads all bytes until the end of the stream and returns them in an array of bytes.
	 *
	 * @return a byte[] containing all bytes until the end of the stream.
	 */
	byte[] readUntilEnd( byte[] buffer );

	/**
	 * Reads bytes.
	 *
	 * @param bytes the buffer to fill.
	 */
	void readBytes( byte[] bytes );

	/**
	 * Reads bytes.
	 *
	 * @param count the number of bytes to read.
	 */
	byte[] readBytes( int count );

	/**
	 * Reads one byte.
	 */
	Byte readByte();

	interface Defaults extends BinaryStreamReader
	{
		@Override default int readBuffer( byte[] buffer )
		{
			return readBuffer( buffer, 0, buffer.length );
		}

		@Override default byte[] readUntilEnd( byte[] buffer )
		{
			int offset = 0;
			for( ; ; )
			{
				int bytesRead = readBuffer( buffer, offset, buffer.length - offset );
				assert bytesRead >= 0;
				if( bytesRead == 0 )
					break;
				offset += bytesRead;
				if( offset >= buffer.length )
					buffer = Arrays.copyOf( buffer, buffer.length + buffer.length );
			}
			return Arrays.copyOf( buffer, offset );
		}

		@Override default void readBytes( byte[] bytes )
		{
			for( int offset = 0; offset < bytes.length; )
			{
				int count = readBuffer( bytes, offset, bytes.length - offset );
				if( count <= 0 )
					throw new RuntimeException(); //premature EOF
				offset += count;
			}
		}

		@Override default byte[] readBytes( int count )
		{
			byte[] bytes = new byte[count];
			readBytes( bytes );
			return bytes;
		}

		@Override default Byte readByte()
		{
			byte[] bytes = readBytes( 1 );
			return bytes[0];
		}
	}
}
