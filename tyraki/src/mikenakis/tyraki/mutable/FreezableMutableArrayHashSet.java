package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.tyraki.FreezableArrayHashSet;
import mikenakis.tyraki.FreezableHashSet;
import mikenakis.tyraki.FreezableList;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableArrayHashSet;
import mikenakis.tyraki.exceptions.DuplicateElementException;

import java.util.Optional;

/**
 * Array Hash Set.
 *
 * @author michael.gr
 */
//IntellijIdea blooper: good code red: "Class must either be declared abstract or implement abstract method m in I"
final class FreezableMutableArrayHashSet<E> extends AbstractMutableCollection<E> implements FreezableArrayHashSet.Defaults<E>
{
	private boolean frozen = false;
	private final FreezableList<E> list;
	private final FreezableHashSet<E> hashSet;

	FreezableMutableArrayHashSet( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super E> hasher,
		EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		list = mutableCollections.newArrayList( initialCapacity, equalityComparator );
		hashSet = mutableCollections.newHashSet( initialCapacity, fillFactor, hasher, equalityComparator );
	}

	@Override public boolean isFrozen()
	{
		return frozen;
	}

	@Override public void freeze()
	{
		assert !frozen;
		list.freeze();
		hashSet.freeze();
		frozen = true;
	}

	@Override public UnmodifiableArrayHashSet<E> frozen()
	{
		freeze();
		return this;
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return hashSet.getElementHasher();
	}

	@Override public int size()
	{
		assert canReadAssertion();
		return hashSet.size();
	}

	@Override public Optional<E> tryGet( E key )
	{
		assert key != null;
		assert canReadAssertion();
		return hashSet.tryGet( key );
	}

	@Override public boolean tryAdd( E key )
	{
		assert key != null;
		assert canWriteAssertion();
		if( !hashSet.tryAdd( key ) )
			return false;
		list.add( key );
		return true;
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert canWriteAssertion();
		assert !equalityComparator.equals( oldElement, newElement );
		if( !hashSet.tryReplace( oldElement, newElement ) )
			return false;
		int index = list.indexOf( oldElement );
		list.replaceAt( index, newElement );
		return true;
	}

	@Override public boolean tryRemove( E key )
	{
		assert canWriteAssertion();
		if( !hashSet.tryRemove( key ) )
			return false;
		list.remove( key );
		return true;
	}

	@Override public boolean clear()
	{
		assert canWriteAssertion();
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
		assert canReadAssertion();
		return new MyEnumerator();
	}

	@Override public void replaceAt( int index, E element )
	{
		E oldElement = list.get( index );
		if( equalityComparator.equals( element, oldElement ) )
			return;
		assert canWriteAssertion();
		if( !hashSet.tryAdd( element ) )
			throw new DuplicateElementException( element );
		hashSet.remove( oldElement );
	}

	@Override public void insertAt( int index, E element )
	{
		assert canWriteAssertion();
		if( !hashSet.tryAdd( element ) )
			throw new DuplicateElementException( element );
		list.insertAt( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert canWriteAssertion();
		E oldElement = list.get( index );
		hashSet.remove( oldElement );
		list.removeAt( index );
	}

	@Override public E get( int index )
	{
		assert canReadAssertion();
		return list.get( index );
	}

	private final class MyEnumerator extends AbstractMutableEnumerator<E> implements MutableEnumerator.Decorator<E>
	{
		final MutableEnumerator<E> decoree;

		MyEnumerator()
		{
			super( FreezableMutableArrayHashSet.this.getMutableCollections() );
			decoree = list.newMutableEnumerator();
		}

		@Override public void deleteCurrent()
		{
			assert canWriteAssertion();
			E key = getCurrent();
			hashSet.remove( key );
			decoree.deleteCurrent();
		}

		@Override public MutableEnumerator<E> getDecoratedUnmodifiableEnumerator()
		{
			return decoree;
		}
	}
}
