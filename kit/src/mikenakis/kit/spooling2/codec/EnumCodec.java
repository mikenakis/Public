package mikenakis.kit.spooling2.codec;

import mikenakis.kit.io.stream.binary.BinaryStreamReader;
import mikenakis.kit.io.stream.binary.BinaryStreamWriter;

public abstract class EnumCodec<T> extends Codec<T>
{
	protected EnumCodec()
	{
	}

	@Override public final T defaultInstance()
	{
		return getInstanceFromOrdinal( 0 );
	}

	public abstract int getInstanceCount();

	public abstract T getInstanceFromOrdinal( int ordinal );

	public abstract int getOrdinalFromInstance( T constant );

	@Override public final T instanceFromBinary( BinaryStreamReader binaryStreamReader )
	{
		int ordinal = Codecs.IntegerCodec.instanceFromBinary( binaryStreamReader );
		return getInstanceFromOrdinal( ordinal );
	}

	@Override public final void instanceIntoBinary( T value, BinaryStreamWriter binaryStreamWriter )
	{
		assert value != null;
		int ordinal = getOrdinalFromInstance( value );
		Codecs.IntegerCodec.instanceIntoBinary( ordinal, binaryStreamWriter );
	}

	@Override public final boolean instancesEqual( T left, T right )
	{
		assert isInstance( left );
		assert isInstance( right );
		assert (getOrdinalFromInstance( left ) == getOrdinalFromInstance( right )) == (left == right);
		return left == right;
	}
}
