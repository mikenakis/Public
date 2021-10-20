package mikenakis.kit.io.stream.text;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableTextStreamWriter extends Closeable, TextStreamWriter
{
	interface Defaults extends CloseableTextStreamWriter, Closeable.Defaults, TextStreamWriter.Defaults
	{
	}
}
