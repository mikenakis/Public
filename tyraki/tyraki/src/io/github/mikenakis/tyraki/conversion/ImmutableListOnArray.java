package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.bathyscaphe.annotations.InvariableArray;
import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.ImmutabilityCoherence;
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
final class ImmutableListOnArray<E> extends AbstractUnmodifiableCollection<E> implements UnmodifiableList.Defaults<E>
{
	@InvariableArray private final E[] array;

	ImmutableListOnArray( EqualityComparator<? super E> equalityComparator, E[] array )
	{
		super( equalityComparator );
		this.array = array;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
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
		return 0;
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new EnumeratorOnList<>( this );
	}

	@Override public Coherence coherence()
	{
		return ImmutabilityCoherence.instance;
	}
}
