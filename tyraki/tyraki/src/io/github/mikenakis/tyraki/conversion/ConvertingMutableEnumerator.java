package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.MutableEnumerator;

/**
 * Converts items from one type to another using a {@link Function1}.
 *
 * @author michael.gr
 */
final class ConvertingMutableEnumerator<T, F> implements MutableEnumerator.Defaults<T>
{
	private final MutableEnumerator<F> enumeratorToConvert;
	private final Function1<? extends T,? super F> converter;

	ConvertingMutableEnumerator( MutableEnumerator<F> enumeratorToConvert, Function1<? extends T,? super F> converter )
	{
		this.enumeratorToConvert = enumeratorToConvert;
		this.converter = converter;
	}

	@Override public void deleteCurrent()
	{
		enumeratorToConvert.deleteCurrent();
	}

	@Override public boolean isFinished()
	{
		return enumeratorToConvert.isFinished();
	}

	@Override public T current()
	{
		F item = enumeratorToConvert.current();
		return converter.invoke( item );
	}

	@Override public MutableEnumerator<T> moveNext()
	{
		enumeratorToConvert.moveNext();
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return unmodifiableEnumeratorToString();
	}

	@Override public Coherence coherence()
	{
		return enumeratorToConvert.coherence();
	}
}
