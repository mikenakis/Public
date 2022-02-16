package mikenakis.io.async.binary.stream.writing.helpers;

import mikenakis.io.async.Async;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure0;
import mikenakis.kit.functional.Procedure1;

public interface AsyncBufferStreamWriter extends Async
{
	/**
	 * Writes a buffer.
	 *
	 * @param buffer the buffer to write.
	 */
	void writeBuffer( Buffer buffer, Procedure0 completionHandler, Procedure1<Throwable> errorHandler );
}
