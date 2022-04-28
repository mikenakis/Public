package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;

import java.util.Optional;

/**
 * A converting {@link EqualityComparator}.
 *
 * @author michael.gr
 */
public final class PartiallyConvertingEqualityComparator<T, F> implements EqualityComparator<T>
{
	private final Function1<Optional<? extends F>,? super T> converter;

	public PartiallyConvertingEqualityComparator( Function1<Optional<? extends F>,? super T> converter )
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
		Optional<? extends F> aKey = converter.invoke( a );
		Optional<? extends F> bKey = converter.invoke( b );
		return aKey.equals( bKey );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName() + " " + converter.toString();
	}
}
