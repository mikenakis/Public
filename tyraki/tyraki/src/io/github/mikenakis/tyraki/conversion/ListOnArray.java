package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.coherence.exceptions.MustBeFrozenException;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableList;

/**
 * An {@link UnmodifiableList} which wraps an array.
 *
 * @param <E> the type of the elements.
 *
 * @author michael.gr
 */
final class ListOnArray<E> extends AbstractUnmodifiableCollection<E> implements UnmodifiableList.Defaults<E>
{
	private final E[] array;
	private final boolean frozen;

	ListOnArray( EqualityComparator<? super E> equalityComparator, E[] array, boolean frozen )
	{
		super( equalityComparator );
		this.array = array;
		this.frozen = frozen;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		assert frozen : new MustBeFrozenException( null );
		return true;
	}

	@Override public E get( int index )
	{
		return array[index];
	}

	@Override public int size()
	{
		return array.length;
	}

	@Override public int getModificationCount()
	{
		return 0; //hope nobody has actually modified the array!
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new EnumeratorOnList<>( this );
	}
}
