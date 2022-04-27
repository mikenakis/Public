package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.UnmodifiableArraySet;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableList;

/**
 * Chains array sets together into a single array set.
 *
 * @author michael.gr
 */
class ChainingArraySet<E> extends ChainingCollection<E> implements UnmodifiableArraySet.Defaults<E>
{
	private final UnmodifiableCollection<? extends UnmodifiableArraySet<E>> listsToChain;

	ChainingArraySet( UnmodifiableCollection<? extends UnmodifiableArraySet<E>> listsToChain, EqualityComparator<E> equalityComparator )
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
		throw new IndexOutOfBoundsException();
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
