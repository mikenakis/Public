package mikenakis.io.async.binary.stream;

import mikenakis.io.async.binary.stream.reading.AsyncBinaryStreamReadingDomain;
import mikenakis.io.async.binary.stream.writing.AsyncBinaryStreamWritingDomain;

/**
 * Combines {@link AsyncBinaryStreamReadingDomain} and {@link AsyncBinaryStreamWritingDomain}.
 *
 * @author michael.gr
 */
public interface AsyncBinaryStreamDomain extends AsyncBinaryStreamReadingDomain, AsyncBinaryStreamWritingDomain
{
	interface Defaults extends AsyncBinaryStreamDomain, AsyncBinaryStreamReadingDomain.Defaults, AsyncBinaryStreamWritingDomain.Defaults
	{
	}
}
