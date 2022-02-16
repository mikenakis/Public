package mikenakis.io.async.binary.stream.reading.helpers;

import mikenakis.io.async.Async;
import mikenakis.kit.buffer.Buffer;
import mikenakis.kit.functional.Procedure1;

import java.util.Optional;

public interface AsyncBufferStreamReader extends Async
{
	/**
	 * Reads a buffer.
	 *
	 * @param receiver the method to receive incoming buffers.
	 */
	void readBuffer( Procedure1<Optional<Buffer>> receiver );

	interface Defaults extends AsyncBufferStreamReader
	{
	}
}
