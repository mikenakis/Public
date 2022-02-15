package mikenakis.io.sync.text.writing;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableTextStreamWriter extends Closeable, TextStreamWriter
{
	interface Defaults extends CloseableTextStreamWriter, Closeable.Defaults, TextStreamWriter.Defaults
	{
	}
}
