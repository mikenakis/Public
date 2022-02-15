package mikenakis.io.sync.binary.stream.writing;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableBinaryStreamWriter extends BinaryStreamWriter, Closeable
{
	interface Defaults extends CloseableBinaryStreamWriter, BinaryStreamWriter.Defaults, Closeable.Defaults
	{
	}
}
