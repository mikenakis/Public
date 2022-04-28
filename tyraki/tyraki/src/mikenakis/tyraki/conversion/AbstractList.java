package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableList;

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
