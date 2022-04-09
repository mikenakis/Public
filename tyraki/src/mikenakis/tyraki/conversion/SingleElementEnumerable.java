package mikenakis.tyraki.conversion;

import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

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

	@Override public boolean isFrozenAssertion()
	{
		return true;
	}

	static final class MyEnumerator<E> extends AbstractUnmodifiableEnumerator<E>
	{
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
	}
}
