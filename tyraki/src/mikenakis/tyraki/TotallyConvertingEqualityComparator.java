package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * A converting {@link EqualityComparator}.
 *
 * @author michael.gr
 */
public final class TotallyConvertingEqualityComparator<T, F> implements EqualityComparator<T>
{
	private final TotalConverter<F,? super T> converter;

	public TotallyConvertingEqualityComparator( TotalConverter<F,? super T> converter )
	{
		assert converter != null;
		this.converter = converter;
	}

	@Override public boolean equals( T a, T b )
	{
		assert a != null;
		assert b != null;
		if( a == b )
			return true;
		F aKey = converter.invoke( a );
		F bKey = converter.invoke( b );
		return aKey.equals( bKey );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName() + " " + converter.toString();
	}
}