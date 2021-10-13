package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.conversion.ConversionCollections;
import mikenakis.tyraki.exceptions.DuplicateElementException;
import mikenakis.kit.EqualityComparator;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Mutable collection.
 *
 * @author michael.gr
 */
public interface MutableCollection<E> extends UnmodifiableCollection<E>, MutableEnumerable<E>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param collection the {@link MutableCollection} to down-cast.
	 * @param <T>        the type of the ancestor.
	 * @param <U>        the type of the {@link MutableCollection}.
	 *
	 * @return the same {@link MutableCollection} cast to an ancestral type.
	 */
	static <T, U extends T> MutableCollection<T> downCast( MutableCollection<U> collection )
	{
		@SuppressWarnings( "unchecked" )
		MutableCollection<T> result = (MutableCollection<T>)collection;
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tries to replace an existing element with a new item.
	 *
	 * @param oldElement the existing element to be replaced.
	 * @param newElement the new item to replace the existing element with.
	 *
	 * @return {@code true} if the element was replaced successfully; {@code false} if the element could not be replaced because it was not found.
	 */
	boolean tryReplace( E oldElement, E newElement );

	/**
	 * Tries to add a new item.
	 *
	 * @param element the item to add.
	 *
	 * @return {@code true} if the item was added successfully; {@code false} if the item could not be added because it was already present.
	 */
	boolean tryAdd( E element );

	/**
	 * Tries to remove an element.
	 *
	 * @param element the element to remove.
	 *
	 * @return {@code true} if the element was removed successfully; {@code false} if the element could not be removed because it was not found.
	 */
	boolean tryRemove( E element );

	/**
	 * Clears the collection.
	 *
	 * @return {@code true} if the collection was cleared; {@code false} if the collection was already empty.
	 */
	@Override boolean clear();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds an element.
	 *
	 * @param element the element to add.
	 */
	MutableCollection<E> add( E element );

	/**
	 * Removes an element.
	 *
	 * @param element the element to be removed.
	 */
	void remove( E element );

	/**
	 * Adds or removes an element.
	 *
	 * @param element the element to add.
	 */
	void addOrRemove( E element, boolean add );

	/**
	 * Replaces an element.
	 *
	 * @param oldElement the element to be replaced.
	 * @param newElement the item to replace the existing element with.
	 */
	void replace( E oldElement, E newElement );

	/**
	 * Tries to add all the given elements.
	 *
	 * @param elements   the elements to add.
	 * @param startIndex the starting index of the array from which to start adding.
	 * @param count      the number of elements of the array to add.
	 *
	 * @return {@code true} if all of the given elements were added; {@code false} if one or more elements could not be added because they already existed.
	 */
	int tryAddAll( E[] elements, int startIndex, int count );

	/**
	 * Adds all the given elements.
	 *
	 * @param elements   the elements to add.
	 * @param startIndex the starting index of the array from which to start adding.
	 * @param count      the number of elements of the array to add.
	 */
	void addAll( E[] elements, int startIndex, int count );

	/**
	 * Tries to add all the given elements.
	 *
	 * @param elements the elements to add.
	 *
	 * @return {@code true} if all of the given elements were added; {@code false} if one or more elements could not be added because they already existed.
	 */
	int tryAddAll( E[] elements );

	/**
	 * Adds all the given elements.
	 *
	 * @param elements   the elements to add.
	 * @param startIndex the starting index of the array from which to start adding.
	 */
	void addAll( E[] elements, int startIndex );

	/**
	 * Tries to add all the given elements.
	 *
	 * @param other the elements to add.
	 *
	 * @return the number of elements that were actually added; may be less than the number of elements passed if one or more elements could not be added because they already existed.
	 */
	int tryAddAll( UnmodifiableEnumerable<? extends E> other );

	/**
	 * Adds all the given elements.
	 *
	 * @param other the elements to add.
	 */
	MutableCollection<E> addAll( UnmodifiableEnumerable<? extends E> other );

	/**
	 * 'Intern's an element.
	 *
	 * @param element the element to intern.
	 *
	 * @return the interned element.
	 */
	E intern( E element );

	<T> MutableCollection<T> mutableConverted( TotalConverter<? extends T,? super E> converter, TotalConverter<E,? super T> reverter );

	/**
	 * Default methods for {@link MutableCollection}.
	 * <p>
	 * PEARL: when JRE 1.8.0_05 was latest, we could not use {@code assert} because of "java.lang.ClassFormatError: Illegal field modifiers in class <X>: 0x1018"
	 */
	interface Defaults<E> extends MutableCollection<E>, UnmodifiableCollection.Defaults<E>, MutableEnumerable.Defaults<E>
	{
		@Override default MutableCollection<E> add( E element )
		{
			boolean ok = tryAdd( element );
			assert ok : new DuplicateElementException( element );
			return this;
		}

		@Override default void remove( E element )
		{
			boolean ok = tryRemove( element );
			assert ok : new NoSuchElementException();
		}

		@Override default void addOrRemove( E element, boolean add )
		{
			if( add )
				add( element );
			else
				remove( element );
		}

		@Override default boolean tryReplace( E oldElement, E newElement )
		{
			if( contains( newElement ) )
				return false;
			if( !tryRemove( oldElement ) )
				return false;
			add( newElement );
			return true;
		}

		@Override default void replace( E oldElement, E newElement )
		{
			boolean ok = tryReplace( oldElement, newElement );
			assert ok : new NoSuchElementException( Objects.toString( oldElement ) );
		}

		@Override default int tryAddAll( E[] elements, int startIndex, int count )
		{
			assert canWriteAssertion();
			assert startIndex >= 0 && startIndex < elements.length;
			assert count > 0 && startIndex + count <= elements.length;
			int addCount = 0;
			int endIndex = startIndex + count;
			for( int i = startIndex; i < endIndex; i++ )
				if( tryAdd( elements[i] ) )
					addCount++;
			return addCount;
		}

		@Override default void addAll( E[] elements, int startIndex, int count )
		{
			int added = tryAddAll( elements, startIndex, count );
			assert added == count : new RuntimeException( "only " + added + " out of " + count + " elements added" );
		}

		@Override default int tryAddAll( E[] elements )
		{
			return tryAddAll( elements, 0, elements.length );
		}

		@Override default int tryAddAll( UnmodifiableEnumerable<? extends E> other )
		{
			assert canWriteAssertion();
			int addCount = 0;
			for( E element : other )
				if( tryAdd( element ) )
					addCount++;
			return addCount;
		}

		@Override default MutableCollection<E> addAll( UnmodifiableEnumerable<? extends E> other )
		{
			assert canWriteAssertion();
			for( E element : other )
				add( element );
			return this;
		}

		@Override default void addAll( E[] elements, int startIndex )
		{
			addAll( elements, startIndex, elements.length - startIndex );
		}

		@Override default boolean clear()
		{
			assert canWriteAssertion();
			boolean workDone = false;
			while( !isEmpty() )
			{
				E element = fetchFirstElement();
				remove( element );
				workDone = true;
			}
			return workDone;
		}

		@Override default E intern( E element )
		{
			assert canWriteAssertion();
			Optional<E> existingElement = tryGet( element );
			if( existingElement.isPresent() )
				return existingElement.get();
			add( element );
			return element;
		}

		@Override default <T> MutableCollection<T> mutableConverted( TotalConverter<? extends T,? super E> converter, TotalConverter<E,? super T> reverter )
		{
			EqualityComparator<T> equalityComparator = new TotallyConvertingEqualityComparator<>( reverter );
			return ConversionCollections.newConvertingMutableCollection( this, converter, reverter, equalityComparator );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Decorator for {@link MutableCollection}.
	 */
	interface Decorator<E> extends Defaults<E>, UnmodifiableCollection.Decorator<E>, MutableEnumerable.Decorator<E>
	{
		MutableCollection<E> getDecoratedMutableCollection();

		@Override default MutableEnumerable<E> getDecoratedMutableEnumerable()
		{
			return getDecoratedMutableCollection();
		}

		@Override default UnmodifiableEnumerable<E> getDecoratedUnmodifiableEnumerable()
		{
			return getDecoratedMutableCollection();
		}

		@Override default UnmodifiableCollection<E> getDecoratedUnmodifiableCollection()
		{
			return getDecoratedMutableCollection();
		}

		@Override default boolean tryReplace( E oldElement, E newElement )
		{
			MutableCollection<E> decoree = getDecoratedMutableCollection();
			return decoree.tryReplace( oldElement, newElement );
		}

		@Override default boolean tryAdd( E element )
		{
			MutableCollection<E> decoree = getDecoratedMutableCollection();
			return decoree.tryAdd( element );
		}

		@Override default boolean tryRemove( E element )
		{
			MutableCollection<E> decoree = getDecoratedMutableCollection();
			return decoree.tryRemove( element );
		}

		@Override default boolean clear()
		{
			MutableCollection<E> decoree = getDecoratedMutableCollection();
			return decoree.clear();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Canary class.
	 * <p>
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

		@Override public MutableCollection<E> getDecoratedMutableCollection()
		{
			return this;
		}
	}
}
