package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Mutable List.
 *
 * @author michael.gr
 */
public interface MutableList<E> extends MutableCollection<E>, UnmodifiableList<E>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param list the {@link MutableList} to down-cast.
	 * @param <T>  the type of the ancestor.
	 * @param <U>  the type of the {@link MutableList}.
	 *
	 * @return the same {@link MutableList} cast to an ancestral type.
	 */
	static <T, U extends T> MutableList<T> downCast( MutableList<U> list )
	{
		@SuppressWarnings( "unchecked" )
		MutableList<T> result = (MutableList<T>)list;
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces an element at a given index with a given item.
	 *
	 * @param index   the index of the element to be replaced
	 * @param element the item to replace the existing element
	 */
	void replaceAt( int index, E element );

	/**
	 * Inserts an item at a given index.
	 *
	 * @param index   the index at which to insert the new item.
	 * @param element the item to insert.
	 */
	void insertAt( int index, E element );

	/**
	 * Removes an element at a given index.
	 *
	 * @param index the index of the element to remove.
	 */
	void removeAt( int index );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override MutableList<E> add( E element );

	/**
	 * Sorts the elements using a given {@link Comparator}.
	 *
	 * @param comparator the {@link Comparator} to use for sorting.
	 */
	void sort( Comparator<? super E> comparator );

	/**
	 * Reverses the order of elements in the list.
	 */
	void revert();

	/**
	 * Removes a range of elements from a given start index until the end of the {@link MutableList}.
	 *
	 * @param start the index of the first element to remove.
	 */
	void removeRange( int start );

	/**
	 * Removes a range of elements.
	 *
	 * @param start the index of the first element to remove.
	 * @param count the number of elements to remove.
	 */
	void removeRange( int start, int count );

	/**
	 * Swaps two elements.
	 *
	 * @param index1 the index of the first element to swap.
	 * @param index2 the index of the second element to swap.
	 */
	void swapAt( int index1, int index2 );

	/**
	 * Moves an element from a given index to a given index.  The destination index must not be equal to the source index, nor equal to the source index plus one.  If the element
	 * is moved down, then its new index will be the given index.  If the element is moved up, then its new index will be the given index minus one.
	 *
	 * @param fromIndex the index of the element to move.  Must be 0 <= fromIndex < {@link MutableList#size()}.
	 * @param toIndex   the index to move the element to.  Must be 0 <= toIndex <= {@link MutableList#size()}.
	 *
	 * @return the element that was moved.
	 */
	E moveAt( int fromIndex, int toIndex );

	@Override <T extends E> MutableList<T> upCast();

	/**
	 * Gets an element at a given index and removes it.
	 *
	 * @param index the index of the element to invoke and remove.
	 */
	E getAndRemove( int index );

	/**
	 * Removes the last element from the list.
	 */
	void removeLast();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for {@link MutableList}.
	 */
	interface Defaults<E> extends MutableList<E>, MutableCollection.Defaults<E>, UnmodifiableList.Defaults<E>
	{
		@Override default boolean tryAdd( E element )
		{
			int index = size();
			insertAt( index, element );
			return true;
		}

		@Override default MutableList<E> add( E element )
		{
			int index = size();
			insertAt( index, element );
			return this;
		}

		@Override default void sort( Comparator<? super E> comparator )
		{
			E[] array = toArrayOfObject();
			Arrays.sort( array, comparator );
			for( int i = 0;  i < array.length;  i++ )
				replaceAt( i, array[i] );
		}

		@Override default void revert()
		{
			int length = size();
			for( int hi = length - 1, lo = 0; lo < hi; lo++, hi-- )
			{
				E temp = get( lo );
				replaceAt( lo, get( hi ) );
				replaceAt( hi, temp );
			}
		}

		@Override default void removeRange( int start )
		{
			removeRange( start, size() - start );
		}

		@Override default void removeRange( int start, int count )
		{
			assert start >= 0 : new IndexOutOfBoundsException();
			assert count >= 0 : new IllegalArgumentException();
			for( int i = start + count - 1; i >= start; i-- )
				removeAt( i );
		}

		@Override default void swapAt( int index1, int index2 )
		{
			E element1 = get( index1 );
			E element2 = get( index2 );
			replaceAt( index1, element2 );
			replaceAt( index2, element1 );
		}

		@Override default boolean tryReplace( E oldElement, E newElement )
		{
			int index = indexOf( oldElement );
			if( index == -1 )
				return false;
			replaceAt( index, newElement );
			return true;
		}

		@Override default boolean tryRemove( E element )
		{
			int index = indexOf( element );
			if( index == -1 )
				return false;
			removeAt( index );
			return true;
		}

		@Override default E moveAt( int fromIndex, int toIndex )
		{
			assert fromIndex >= 0;
			assert fromIndex < size();
			assert toIndex >= 0;
			assert toIndex <= size();
			assert fromIndex != toIndex;
			assert fromIndex != toIndex - 1;
			E item = getAndRemove( fromIndex );
			if( toIndex > fromIndex )
				toIndex--;
			insertAt( toIndex, item );
			return item;
		}

		@Override default <T extends E> MutableList<T> upCast()
		{
			@SuppressWarnings( "unchecked" )
			MutableList<T> result = (MutableList<T>)this;
			return result;
		}

		@Override default E getAndRemove( int index )
		{
			E element = get( index );
			removeAt( index );
			return element;
		}

		@Override default void removeLast()
		{
			int index = size() - 1;
			removeAt( index );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link MutableList}.
	 */
	interface Decorator<E> extends Defaults<E>, MutableCollection.Decorator<E>, UnmodifiableList.Decorator<E>
	{
		MutableList<E> getDecoratedMutableList();

		@Override default UnmodifiableCollection<E> getDecoratedUnmodifiableCollection()
		{
			return getDecoratedMutableList();
		}

		@Override default MutableCollection<E> getDecoratedMutableCollection()
		{
			return getDecoratedMutableList();
		}

		@Override default UnmodifiableList<E> getDecoratedUnmodifiableList()
		{
			return getDecoratedMutableList();
		}

		@Override default void replaceAt( int index, E element )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			decoree.replaceAt( index, element );
		}

		@Override default void insertAt( int index, E element )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			decoree.insertAt( index, element );
		}

		@Override default void removeAt( int index )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			decoree.removeAt( index );
		}

		@Override default boolean tryAdd( E element )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			return decoree.tryAdd( element );
		}

		@Override default boolean tryRemove( E element )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			return decoree.tryRemove( element );
		}

		@Override default boolean tryReplace( E oldElement, E newElement )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			return decoree.tryReplace( oldElement, newElement );
		}

		@Override default UnmodifiableList<E> chained( UnmodifiableList<E> list )
		{
			MutableList<E> decoree = getDecoratedMutableList();
			return decoree.chained( list );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Canary class.
	 *
	 * This is a concrete class to make sure that if there are problems with the interface making it impossible to inherit from, they will be caught by the compiler at the
	 * earliest point possible, and not when compiling some derived class.
	 */
	@ExcludeFromJacocoGeneratedReport @SuppressWarnings( "unused" )
	final class Canary<E> implements Decorator<E>
	{
		@Override public boolean canWriteAssertion()
		{
			return true;
		}

		@Override public MutableList<E> getDecoratedMutableList()
		{
			return this;
		}
	}
}
