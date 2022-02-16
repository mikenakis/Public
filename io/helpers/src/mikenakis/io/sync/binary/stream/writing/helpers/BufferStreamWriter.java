package mikenakis.io.sync.binary.stream.writing.helpers;

import mikenakis.kit.buffer.Buffer;

public interface BufferStreamWriter
{
	/**
	 * Writes a buffer.
	 *
	 * @param buffer the buffer to write.
	 */
	void writeBuffer( Buffer buffer );

	interface Defaults extends BufferStreamWriter
	{
	}
}
