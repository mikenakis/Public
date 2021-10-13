package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function2;
import mikenakis.tyraki.TotalConverterWithIndex;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * A decorator of {@link UnmodifiableEnumerable} which converts items from one type to another using a {@link Function2}.
 *
 * @param <T> the type to convert to
 * @param <F> the type to convert from
 *
 * @author michael.gr
 */
final class ConvertingEnumerable<T, F> extends AbstractUnmodifiableEnumerable<T>
{
	private final UnmodifiableEnumerable<F> enumerableToConvert;
	private final TotalConverterWithIndex<? extends T,? super F> converter;

	ConvertingEnumerable( UnmodifiableEnumerable<F> enumerableToConvert, TotalConverterWithIndex<? extends T,? super F> converter )
	{
		assert enumerableToConvert != null;
		assert converter != null;
		this.enumerableToConvert = enumerableToConvert;
		this.converter = converter;
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> enumeratorToConvert = enumerableToConvert.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( enumeratorToConvert, converter );
	}

	@Override public int getModificationCount()
	{
		return enumerableToConvert.getModificationCount();
	}

	@Override public boolean isFrozen()
	{
		return enumerableToConvert.isFrozen();
	}
}
