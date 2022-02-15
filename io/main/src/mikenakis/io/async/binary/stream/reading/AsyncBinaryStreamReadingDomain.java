package mikenakis.io.async.binary.stream.reading;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.file.Path;

/**
 * Factory for {@link AsyncBinaryStreamReader}
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamReadingDomain
{
	CloseableAsyncBinaryStreamReader newReaderOnAsynchronousByteChannel( AsynchronousByteChannel asynchronousByteChannel, boolean handOff );

	CloseableAsyncBinaryStreamReader newReaderOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends AsyncBinaryStreamReadingDomain
	{
	}
}
