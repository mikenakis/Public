package mikenakis.kit.buffer;

import java.util.Comparator;

public final class CaseInsensitiveBufferComparator implements Comparator<Buffer>
{
	public static final Comparator<Buffer> INSTANCE = new CaseInsensitiveBufferComparator();

	private CaseInsensitiveBufferComparator()
	{
	}

	@Override public int compare( Buffer a, Buffer b )
	{
		int aLength = a.getLength();
		int bLength = b.getLength();
		int commonLength = Math.min( aLength, bLength );
		for( int i = 0; i < commonLength; i++ )
		{
			byte ai = a.byteAt( i );
			byte uai = Buffer.toUpper( ai );
			byte bi = b.byteAt( i );
			byte ubi = Buffer.toUpper( bi );
			int d = Byte.compare( uai, ubi );
			if( d != 0 )
				return d;
		}
		//noinspection SubtractionInCompareTo
		return aLength - bLength;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
