package mikenakis.io.sync.text.reading;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableTextStreamReader extends Closeable, TextStreamReader
{
	interface Defaults extends CloseableTextStreamReader, Closeable.Defaults, TextStreamReader.Defaults
	{
	}
}
