package io.github.mikenakis.tyraki;

import java.util.Optional;

/**
 * (by definition mutable) {@link Queue }.
 *
 * @author michael.gr
 */
public interface Queue<E>
{
	int getModificationCount();
	int size();
	boolean enqueue( E element );
	Optional<E> tryDequeue();
	boolean clear();

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	E dequeue();
	boolean isEmpty();

	interface Defaults<E> extends Queue<E>
	{
		@Override default E dequeue()
		{
			return tryDequeue().orElseThrow();
		}

		@Override default boolean isEmpty()
		{
			return size() == 0;
		}
	}
}
