package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.function.Predicate;

/**
 * A decorator of {@link UnmodifiableEnumerator} which filters elements using a {@link Predicate}.
 *
 * @author michael.gr
 */
class FilteringEnumerator<E> extends AbstractUnmodifiableEnumerator<E>
{
	private final UnmodifiableEnumerator<E> enumeratorToFilter;
	private final Predicate<? super E> predicate;

	FilteringEnumerator( UnmodifiableEnumerator<E> enumeratorToFilter, Predicate<? super E> predicate )
	{
		this.enumeratorToFilter = enumeratorToFilter;
		this.predicate = predicate;
		skipToMatch();
	}

	@Override public boolean isFinished()
	{
		return enumeratorToFilter.isFinished();
	}

	@Override public E current()
	{
		return enumeratorToFilter.current();
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		enumeratorToFilter.moveNext();
		skipToMatch();
		return this;
	}

	private void skipToMatch()
	{
		while( !enumeratorToFilter.isFinished() && !predicate.test( enumeratorToFilter.current() ) )
			enumeratorToFilter.moveNext();
	}

	@Override public Coherence coherence()
	{
		return enumeratorToFilter.coherence();
	}
}
