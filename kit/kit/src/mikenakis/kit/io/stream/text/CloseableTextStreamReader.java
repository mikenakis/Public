package mikenakis.kit.io.stream.text;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableTextStreamReader extends Closeable, TextStreamReader
{
	interface Defaults extends CloseableTextStreamReader, Closeable.Defaults, TextStreamReader.Defaults
	{
	}
}
