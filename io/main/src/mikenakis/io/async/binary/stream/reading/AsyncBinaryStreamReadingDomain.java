package mikenakis.io.async.binary.stream.reading;

import mikenakis.kit.lifetime.CloseableWrapper;

import java.nio.channels.AsynchronousByteChannel;
import java.nio.file.Path;

/**
 * Factory for {@link AsyncBinaryStreamReader}
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamReadingDomain
{
	CloseableWrapper<AsyncBinaryStreamReader> newReaderOnAsynchronousByteChannel( AsynchronousByteChannel asynchronousByteChannel, boolean handOff );

	CloseableWrapper<AsyncBinaryStreamReader> newReaderOnPath( Path path );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults extends AsyncBinaryStreamReadingDomain
	{
	}
}
