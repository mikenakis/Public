package mikenakis.io.async.binary.stream.writing;

import mikenakis.kit.lifetime.Closeable;

/**
 *  {@link Closeable} {@link AsyncBinaryStreamWriter}.
 *
 * @author michael.gr
 */
public interface CloseableAsyncBinaryStreamWriter extends AsyncBinaryStreamWriter, Closeable
{
	interface Defaults extends CloseableAsyncBinaryStreamWriter, AsyncBinaryStreamWriter.Defaults, Closeable.Defaults
	{
	}
}
