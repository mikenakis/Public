package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Optional;

/**
 * A converting {@link EqualityComparator}.
 *
 * @author michael.gr
 */
public final class PartiallyConvertingEqualityComparator<T, F> implements EqualityComparator<T>
{
	private final PartialConverter<? extends F,? super T> converter;

	public PartiallyConvertingEqualityComparator( PartialConverter<? extends F,? super T> converter )
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
		Optional<? extends F> aKey = converter.convert( a );
		Optional<? extends F> bKey = converter.convert( b );
		return aKey.equals( bKey );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName() + " " + converter.toString();
	}
}
