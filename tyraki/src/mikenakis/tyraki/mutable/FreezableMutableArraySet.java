package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * A Set implemented using an array.
 *
 * @author michael.gr
 */
final class FreezableMutableArraySet<E> extends AbstractMutableCollection<E> implements MutableArraySet.Defaults<E>
{
	private final MutableList<E> list;

	FreezableMutableArraySet( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator, int initialCapacity )
	{
		super( mutableCollections, equalityComparator );
		list = mutableCollections.newArrayList( initialCapacity, equalityComparator );
	}

	@Override public int size()
	{
		return list.size();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		assert canReadAssertion();
		return list.tryGet( element );
	}

	@Override public int getModificationCount()
	{
		return list.getModificationCount();
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert canMutateAssertion();
		if( list.contains( element ) )
			return Optional.of( element );
		list.add( element );
		return Optional.empty();
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert canMutateAssertion();
		if( !list.tryRemove( oldElement ) )
			return false;
		list.add( newElement );
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert canMutateAssertion();
		return list.tryRemove( element );
	}

	@Override public boolean clear()
	{
		assert canMutateAssertion();
		if( list.isEmpty() )
			return false;
		list.clear();
		return true;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert canReadAssertion();
		return list.newMutableEnumerator();
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@Override public void replaceAt( int index, E element )
	{
		assert canMutateAssertion();
		if( getEqualityComparator().equals( element, list.get( index ) ) )
			return;
		assert !contains( element );
		list.replaceAt( index, element );
	}

	@Override public void insertAt( int index, E element )
	{
		assert canMutateAssertion();
		assert !contains( element );
		list.insertAt( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert canMutateAssertion();
		list.removeAt( index );
	}

	@Override public E get( int index )
	{
		assert canReadAssertion();
		return list.get( index );
	}
}
