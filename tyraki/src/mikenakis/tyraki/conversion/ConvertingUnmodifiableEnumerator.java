package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function2;
import mikenakis.tyraki.TotalConverterWithIndex;
import mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * A decorator of {@link UnmodifiableEnumerator} which converts items from one type to another using a {@link Function2}.
 *
 * @param <T> the type to convert to
 * @param <F> the type to convert from
 *
 * @author michael.gr
 */
class ConvertingUnmodifiableEnumerator<T, F> extends AbstractUnmodifiableEnumerator<T>
{
	private final UnmodifiableEnumerator<F> enumeratorToConvert;
	private final TotalConverterWithIndex<? extends T,? super F> converter;
	private int index = 0;

	ConvertingUnmodifiableEnumerator( UnmodifiableEnumerator<F> enumeratorToConvert, TotalConverterWithIndex<? extends T,? super F> converter )
	{
		assert enumeratorToConvert != null;
		assert converter != null;
		this.enumeratorToConvert = enumeratorToConvert;
		this.converter = converter;
	}

	@Override public boolean isFinished()
	{
		return enumeratorToConvert.isFinished();
	}

	@Override public T current()
	{
		F itemToConvert = enumeratorToConvert.current();
		return converter.invoke( index, itemToConvert );
	}

	@Override public UnmodifiableEnumerator<T> moveNext()
	{
		enumeratorToConvert.moveNext();
		index++;
		return this;
	}
}
