package mikenakis.tyraki.mutable;

import mikenakis.tyraki.FreezableArraySet;
import mikenakis.tyraki.FreezableList;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableArraySet;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * A Set implemented using an array.
 *
 * @author michael.gr
 */
final class FreezableMutableArraySet<E> extends AbstractMutableCollection<E> implements FreezableArraySet.Defaults<E>
{
	private final FreezableList<E> list;

	FreezableMutableArraySet( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator, int initialCapacity )
	{
		super( mutableCollections, equalityComparator );
		list = mutableCollections.newArrayList( initialCapacity, equalityComparator );
	}

	@Override public void freeze()
	{
		list.freeze();
	}

	@Override public boolean isFrozen()
	{
		return list.isFrozen();
	}

	@Override public UnmodifiableArraySet<E> frozen()
	{
		freeze();
		return this;
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

	@Override public boolean tryAdd( E element )
	{
		assert canWriteAssertion();
		if( list.contains( element ) )
			return false;
		list.add( element );
		return true;
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert canWriteAssertion();
		if( !list.tryRemove( oldElement ) )
			return false;
		list.add( newElement );
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert canWriteAssertion();
		return list.tryRemove( element );
	}

	@Override public boolean clear()
	{
		assert canWriteAssertion();
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
		assert canWriteAssertion();
		if( getEqualityComparator().equals( element, list.get( index ) ) )
			return;
		assert !contains( element );
		list.replaceAt( index, element );
	}

	@Override public void insertAt( int index, E element )
	{
		assert canWriteAssertion();
		assert !contains( element );
		list.insertAt( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert canWriteAssertion();
		list.removeAt( index );
	}

	@Override public E get( int index )
	{
		assert canReadAssertion();
		return list.get( index );
	}
}
