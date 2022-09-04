package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.ImmutabilityCoherence;
import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * An {@link UnmodifiableEnumerable} which enumerates a single element.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
final class SingleElementEnumerable<E> implements UnmodifiableEnumerable.Defaults<E>
{
	private final E element;

	SingleElementEnumerable( E element )
	{
		this.element = element;
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new MyEnumerator<>( element );
	}

	@Override public int getModificationCount()
	{
		return 0;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return true;
	}

	@Override public Coherence coherence()
	{
		return ImmutabilityCoherence.instance;
	}

	static final class MyEnumerator<E> extends AbstractUnmodifiableEnumerator<E>
	{
		private final Coherence coherence = ThreadLocalCoherence.instance();
		private E element;

		MyEnumerator( E element )
		{
			this.element = element;
		}

		@Override public boolean isFinished()
		{
			return element == null;
		}

		@Override public E current()
		{
			assert !isFinished();
			return element;
		}

		@Override public UnmodifiableEnumerator<E> moveNext()
		{
			assert !isFinished();
			element = null;
			return this;
		}

		@Override public Coherence coherence()
		{
			return coherence;
		}
	}
}
