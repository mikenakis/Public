package mikenakis.kit.io.stream.binary;

import mikenakis.kit.lifetime.Closeable;

public interface CloseableBinaryStreamWriter extends BinaryStreamWriter, Closeable
{
	interface Defaults extends CloseableBinaryStreamWriter, BinaryStreamWriter.Defaults, Closeable.Defaults
	{
	}
}
