package mikenakis.io.async.binary.stream.reading;

import mikenakis.kit.lifetime.Closeable;

/**
 * {@link Closeable} {@link AsyncBinaryStreamReader}.
 *
 * @author michael.gr
 */
public interface CloseableAsyncBinaryStreamReader extends AsyncBinaryStreamReader, Closeable
{
	interface Defaults extends CloseableAsyncBinaryStreamReader, AsyncBinaryStreamReader.Defaults, Closeable.Defaults
	{
	}
}
