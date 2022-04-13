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
	private final UnmodifiableEnumerable<F> enumerable;
	private final TotalConverterWithIndex<? extends T,? super F> converter;

	ConvertingEnumerable( UnmodifiableEnumerable<F> enumerable, TotalConverterWithIndex<? extends T,? super F> converter )
	{
		assert enumerable != null;
		assert converter != null;
		this.enumerable = enumerable;
		this.converter = converter;
	}

	@Override public boolean isImmutableAssertion()
	{
		return enumerable.isImmutableAssertion();
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> enumeratorToConvert = enumerable.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( enumeratorToConvert, converter );
	}

	@Override public int getModificationCount()
	{
		return enumerable.getModificationCount();
	}
}
