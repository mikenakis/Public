package mikenakis.kit.io;

/**
 * Synchronous Binary Stream Writer.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface BinaryStreamWriter
{
	/**
	 * Writes bytes.
	 *
	 * @param bytes the buffer containing the bytes to write.
	 * @param index the offset within the buffer to start writing bytes from.
	 * @param count how many bytes to write.
	 */
	void writeBytes( byte[] bytes, int index, int count );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes bytes.
	 *
	 * @param bytes the buffer containing the bytes to write.
	 */
	void writeBytes( byte[] bytes );

	/**
	 * Writes a single byte.
	 *
	 * @param value the byte to write.
	 */
	void writeByte( byte value );

	interface Defaults extends BinaryStreamWriter
	{
		@Override default void writeBytes( byte[] bytes )
		{
			writeBytes( bytes, 0, bytes.length );
		}

		@Override default void writeByte( byte value )
		{
			byte[] bytes = new byte[1];
			bytes[0] = value;
			writeBytes( bytes );
		}
	}
}
