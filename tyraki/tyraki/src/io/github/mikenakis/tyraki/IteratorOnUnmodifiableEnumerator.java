package io.github.mikenakis.tyraki;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link Iterator} on a {@link UnmodifiableEnumerator}.
 *
 * Every call to next() must be preceded by one and only one call to hasNext().
 *
 * @author michael.gr
 */
public final class IteratorOnUnmodifiableEnumerator<T> implements Iterator<T>
{
	private final UnmodifiableEnumerator<T> enumerator;
	private boolean mustMoveNext = false;

	/**
	 * Initializes a new instance of {@link IteratorOnUnmodifiableEnumerator}.
	 *
	 * @param enumerator the {@link UnmodifiableEnumerator} to delegate to.
	 */
	public IteratorOnUnmodifiableEnumerator( UnmodifiableEnumerator<T> enumerator )
	{
		this.enumerator = enumerator;
	}

	@Override public boolean hasNext()
	{
		if( mustMoveNext )
		{
			mustMoveNext = false;
			enumerator.moveNext();
		}
		return !enumerator.isFinished();
	}

	@SuppressWarnings( "IteratorNextCanNotThrowNoSuchElementException" )
	@Override public T next()
	{
		assert !mustMoveNext; //next() was invoked without first invoking hasNext().  Strictly speaking, this is not an error, but it should not really be happening.
		assert !enumerator.isFinished() : new NoSuchElementException();
		mustMoveNext = true;
		return enumerator.current();
	}

	@Override public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
