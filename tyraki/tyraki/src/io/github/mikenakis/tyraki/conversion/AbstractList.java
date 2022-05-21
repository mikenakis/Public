package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableList;

/**
 * Abstract {@link UnmodifiableList}.
 *
 * @author michael.gr
 */
abstract class AbstractList<E> extends AbstractUnmodifiableCollection<E> implements UnmodifiableList.Defaults<E>
{
	AbstractList( EqualityComparator<? super E> equalityComparator )
	{
		super( equalityComparator );
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new EnumeratorOnList<>( this );
	}
}
