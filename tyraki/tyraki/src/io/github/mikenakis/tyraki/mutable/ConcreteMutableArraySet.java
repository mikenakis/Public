package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.MutableArraySet;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * A Set implemented using an array.
 *
 * @author michael.gr
 */
//IntellijIdea blooper: good code red: "Class must either be declared abstract or implement abstract method m in I"
final class ConcreteMutableArraySet<E> extends AbstractMutableCollection<E> implements MutableArraySet.Defaults<E>
{
	private final MutableList<E> list;

	ConcreteMutableArraySet( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator, int initialCapacity )
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
		assert mustBeReadableAssertion();
		return list.tryGet( element );
	}

	@Override public int getModificationCount()
	{
		return list.getModificationCount();
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert mustBeWritableAssertion();
		if( list.contains( element ) )
			return Optional.of( element );
		list.add( element );
		return Optional.empty();
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert mustBeWritableAssertion();
		if( !list.tryRemove( oldElement ) )
			return false;
		list.add( newElement );
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert mustBeWritableAssertion();
		return list.tryRemove( element );
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( list.isEmpty() )
			return false;
		list.clear();
		return true;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert mustBeReadableAssertion();
		return list.newMutableEnumerator();
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return newMutableEnumerator();
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@Override public void replaceAt( int index, E element )
	{
		assert mustBeWritableAssertion();
		if( getEqualityComparator().equals( element, list.get( index ) ) )
			return;
		assert !contains( element );
		list.replaceAt( index, element );
	}

	@Override public void insertAt( int index, E element )
	{
		assert mustBeWritableAssertion();
		assert !contains( element );
		list.insertAt( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert mustBeWritableAssertion();
		list.removeAt( index );
	}

	@Override public E get( int index )
	{
		assert mustBeReadableAssertion();
		return list.get( index );
	}
}
