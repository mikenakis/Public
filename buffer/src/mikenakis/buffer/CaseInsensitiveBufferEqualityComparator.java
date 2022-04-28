package mikenakis.buffer;

import mikenakis.kit.EqualityComparator;

public final class CaseInsensitiveBufferEqualityComparator implements EqualityComparator<Buffer>
{
	public static final EqualityComparator<Buffer> INSTANCE = new CaseInsensitiveBufferEqualityComparator();

	private CaseInsensitiveBufferEqualityComparator()
	{
	}

	@Override public boolean equals( Buffer a, Buffer b )
	{
		return CaseInsensitiveBufferComparator.INSTANCE.compare( a, b ) == 0;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
