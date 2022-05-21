package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableList;

/**
 * Chains lists together into a single list.
 *
 * @author michael.gr
 */
class ChainingList<E> extends ChainingCollection<E> implements UnmodifiableList.Defaults<E>
{
	private final UnmodifiableCollection<? extends UnmodifiableList<E>> listsToChain;

	ChainingList( UnmodifiableCollection<? extends UnmodifiableList<E>> listsToChain, EqualityComparator<E> equalityComparator )
	{
		super( UnmodifiableCollection.downCast( listsToChain ), equalityComparator );
		this.listsToChain = listsToChain;
	}

	@Override public final E get( int index )
	{
		for( UnmodifiableList<E> list : listsToChain )
		{
			int size = list.size();
			if( index < size )
				return list.get( index );
			index -= size;
		}
		assert false : new IndexOutOfBoundsException();
		return null;
	}

	@Override public final int indexOf( E element )
	{
		int index = 0;
		for( UnmodifiableList<E> list : listsToChain )
		{
			int thisIndex = list.indexOf( element );
			if( thisIndex != -1 )
				return index + thisIndex;
			index += list.size();
		}
		return -1;
	}
}
