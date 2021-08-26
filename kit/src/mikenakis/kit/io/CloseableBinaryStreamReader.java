package mikenakis.kit.io;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableBinaryStreamReader extends BinaryStreamReader, Closeable
{
	interface Defaults extends CloseableBinaryStreamReader, BinaryStreamReader.Defaults, Closeable.Defaults
	{
	}
}
