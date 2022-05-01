package mikenakis.buffer;

import mikenakis.kit.Hasher;

public final class CaseInsensitiveBufferHasher implements Hasher<Buffer>
{
	public static final Hasher<Buffer> INSTANCE = new CaseInsensitiveBufferHasher();

	private CaseInsensitiveBufferHasher()
	{
	}

	@Override public int getHashCode( Buffer buffer )
	{
		return buffer.caseInsensitiveHashCode();
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
