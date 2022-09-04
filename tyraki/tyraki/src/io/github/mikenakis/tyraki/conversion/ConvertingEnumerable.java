package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.functional.Function2;
import io.github.mikenakis.tyraki.TotalConverterWithIndex;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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

	@Override public boolean mustBeImmutableAssertion()
	{
		return enumerable.mustBeImmutableAssertion();
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

	@Override public Coherence coherence()
	{
		return  enumerable.coherence() ;
	}
}
