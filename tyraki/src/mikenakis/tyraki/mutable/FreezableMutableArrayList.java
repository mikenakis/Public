package mikenakis.tyraki.mutable;

import mikenakis.tyraki.FreezableList;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableList;
import mikenakis.kit.EqualityComparator;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Array List.
 *
 * @author michael.gr
 */
final class FreezableMutableArrayList<E> extends AbstractMutableList<E> implements FreezableList.Defaults<E>
{
	private static final Object[] EMPTY_DATA = {};

	private boolean frozen = false;
	private Object[] elementData; // non-private to simplify nested class access
	private int size;
	private int modificationCount;

	FreezableMutableArrayList( MutableCollections mutableCollections, EqualityComparator<? super E> equalityComparator, int initialCapacity )
	{
		super( mutableCollections, equalityComparator );
		assert initialCapacity >= 0 : new IllegalArgumentException();
		elementData = initialCapacity == 0 ? EMPTY_DATA : new Object[initialCapacity];
		size = 0;
		modificationCount = 0;
	}

	@Override public boolean isFrozen()
	{
		return frozen;
	}

	@Override public void freeze()
	{
		assert !frozen;
		trimToSize();
		frozen = true;
	}

	@Override public UnmodifiableList<E> frozen()
	{
		freeze();
		return this;
	}

	@Override public FreezableList<E> add( E element )
	{
		return FreezableList.Defaults.super.add( element );
	}

	/**
	 * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts to allocate larger arrays may result in OutOfMemoryError: Requested array
	 * size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = 1024 * 1024 * 1024;

	/**
	 * Increases the capacity to ensure that it can hold at least the number of elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 */
	private void grow( int minCapacity )
	{
		assert minCapacity <= MAX_ARRAY_SIZE;
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if( newCapacity < minCapacity )
			newCapacity = minCapacity;
		if( newCapacity > MAX_ARRAY_SIZE )
			newCapacity = MAX_ARRAY_SIZE;
		elementData = Arrays.copyOf( elementData, newCapacity );
	}

	/**
	 * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's current size.  An application can use this operation to minimize the storage of an
	 * <tt>ArrayList</tt> instance.
	 */
	@SuppressWarnings( "unused" )
	public void trimToSize()
	{
		assert canWriteAssertion();
		modificationCount++;
		if( size < elementData.length )
			elementData = Arrays.copyOf( elementData, size );
	}

	private void ensureCapacity( int minCapacity )
	{
		if( minCapacity > elementData.length )
			grow( minCapacity );
	}

	@Override public int size()
	{
		return size;
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	@Override public E get( int index )
	{
		assert index < size : new ArrayIndexOutOfBoundsException( index );
		@SuppressWarnings( "unchecked" )
		E result = (E)elementData[index];
		return result;
	}

	@Override public void replaceAt( int index, E element )
	{
		assert canWriteAssertion();
		//modificationCount++;
		elementData[index] = element;
	}

	@Override public void insertAt( int index, E element )
	{
		assert canWriteAssertion();
		assert index >= 0 : new ArrayIndexOutOfBoundsException();
		assert index <= size : new ArrayIndexOutOfBoundsException();
		modificationCount++;
		ensureCapacity( size + 1 );
		System.arraycopy( elementData, index, elementData, index + 1, size - index );
		elementData[index] = element;
		size++;
	}

	@Override public void removeAt( int index )
	{
		assert canWriteAssertion();
		assert index >= 0 : new ArrayIndexOutOfBoundsException();
		assert index < size : new ArrayIndexOutOfBoundsException();
		modificationCount++;
		int numMoved = size - index - 1;
		if( numMoved > 0 )
			System.arraycopy( elementData, index + 1, elementData, index, numMoved );
		elementData[--size] = null; // clear to let GC do its work
	}

	@Override public boolean clear()
	{
		assert canWriteAssertion();
		if( size == 0 )
			return false;
		modificationCount++;
		for( int i = 0; i < size; i++ )
			elementData[i] = null;
		size = 0;
		return true;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert canReadAssertion();
		return new MyEnumerator( getMutableCollections() );
	}

	final class MyEnumerator extends AbstractMutableEnumerator<E>
	{
		private int expectedModCount;
		private int index;
		private boolean deleted;

		MyEnumerator( MutableCollections mutableCollections )
		{
			super( mutableCollections );
			expectedModCount = modificationCount; //NOTE: checking whether assertions are enabled to skip this is more expensive than just doing this.
			index = 0;
			deleted = false;
		}

		@Override public void deleteCurrent()
		{
			assert modificationCount == expectedModCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert index < size : new IllegalStateException();
			removeAt( index );
			deleted = true;
			expectedModCount++;
			assert modificationCount == expectedModCount; //the list should have updated its mod count.
		}

		@Override public boolean isFinished()
		{
			assert !deleted : new IllegalStateException();
			return index >= size;
		}

		@Override public E getCurrent()
		{
			assert modificationCount == expectedModCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert index < size : new IllegalStateException();
			return get( index );
		}

		@Override public UnmodifiableEnumerator<E> moveNext()
		{
			assert modificationCount == expectedModCount : new ConcurrentModificationException();
			if( deleted )
				deleted = false;
			else
			{
				assert index < size : new IllegalStateException();
				index++;
			}
			return this;
		}
	}

	@Override public Iterator<E> iterator()
	{
		assert canReadAssertion();
		return new MyIterator();
	}

	final class MyIterator implements Iterator<E>
	{
		private int index;

		MyIterator()
		{
			index = 0;
		}

		@Override public boolean hasNext()
		{
			return index < size;
		}

		@SuppressWarnings( "IteratorNextCanNotThrowNoSuchElementException" )
		@Override public E next()
		{
			return get( index++ );
		}
	}
}
