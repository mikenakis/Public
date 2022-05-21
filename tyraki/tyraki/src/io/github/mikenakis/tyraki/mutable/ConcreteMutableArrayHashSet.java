package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.tyraki.AbstractEnumerator;
import io.github.mikenakis.tyraki.MutableArrayHashSet;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableHashSet;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;
import io.github.mikenakis.tyraki.exceptions.DuplicateElementException;

import java.util.Optional;

/**
 * Array Hash Set.
 *
 * @author michael.gr
 */
//IntellijIdea blooper: good code red: "Class must either be declared abstract or implement abstract method m in I"
final class ConcreteMutableArrayHashSet<E> extends AbstractMutableCollection<E> implements MutableArrayHashSet.Defaults<E>
{
	private final MutableList<E> list;
	private final MutableHashSet<E> hashSet;

	ConcreteMutableArrayHashSet( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super E> hasher,
		EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		list = mutableCollections.newArrayList( initialCapacity, equalityComparator );
		hashSet = mutableCollections.newHashSet( initialCapacity, fillFactor, hasher, equalityComparator );
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return hashSet.getElementHasher();
	}

	@Override public int size()
	{
		assert mustBeReadableAssertion();
		return hashSet.size();
	}

	@Override public Optional<E> tryGet( E key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		return hashSet.tryGet( key );
	}

	@Override public Optional<E> tryAdd( E key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Optional<E> existing = hashSet.tryAdd( key );
		if( existing.isPresent() )
			return existing;
		list.add( key );
		return Optional.empty();
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert mustBeWritableAssertion();
		assert !equalityComparator.equals( oldElement, newElement );
		if( !hashSet.tryReplace( oldElement, newElement ) )
			return false;
		int index = list.indexOf( oldElement );
		list.replaceAt( index, newElement );
		return true;
	}

	@Override public boolean tryRemove( E key )
	{
		assert mustBeWritableAssertion();
		if( !hashSet.tryRemove( key ) )
			return false;
		list.remove( key );
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( !hashSet.clear() )
			return false;
		boolean ok = list.clear();
		assert ok;
		return true;
	}

	@Override public int getModificationCount()
	{
		return hashSet.getModificationCount();
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert mustBeReadableAssertion();
		return new MyEnumerator();
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return newMutableEnumerator();
	}

	@Override public void replaceAt( int index, E element )
	{
		E oldElement = list.get( index );
		if( equalityComparator.equals( element, oldElement ) )
			return;
		assert mustBeWritableAssertion();
		if( hashSet.tryAdd( element ).isPresent() )
			throw new DuplicateElementException( element );
		hashSet.remove( oldElement );
	}

	@Override public void insertAt( int index, E element )
	{
		assert mustBeWritableAssertion();
		if( hashSet.tryAdd( element ).isPresent() )
			throw new DuplicateElementException( element );
		list.insertAt( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert mustBeWritableAssertion();
		E oldElement = list.get( index );
		hashSet.remove( oldElement );
		list.removeAt( index );
	}

	@Override public E get( int index )
	{
		assert mustBeReadableAssertion();
		return list.get( index );
	}

	private final class MyEnumerator extends AbstractEnumerator<E> implements MutableEnumerator.Decorator<E>
	{
		final MutableEnumerator<E> decoree;

		MyEnumerator()
		{
			decoree = list.newMutableEnumerator();
		}

		@Override public void deleteCurrent()
		{
			assert mustBeWritableAssertion();
			E key = current();
			hashSet.remove( key );
			decoree.deleteCurrent();
		}

		@Override public MutableEnumerator<E> getDecoratedUnmodifiableEnumerator()
		{
			return decoree;
		}
	}
}
