package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.implementation.ConcreteFreezableCoherence;
import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.functional.Procedure1;
import io.github.mikenakis.live.Mortal;
import io.github.mikenakis.tyraki.conversion.ConversionCollections;
import io.github.mikenakis.tyraki.immutable.ImmutableCollections;
import io.github.mikenakis.tyraki.mutable.MutableCollections;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Unmodifiable Array List.
 *
 * @author michael.gr
 */
public interface UnmodifiableList<E> extends UnmodifiableCollection<E>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param list the {@link UnmodifiableList} to down-cast.
	 * @param <T>  the type of the ancestor.
	 * @param <U>  the type of the {@link UnmodifiableList}.
	 *
	 * @return the same {@link UnmodifiableList} cast to an ancestral type.
	 */
	static <T, U extends T> UnmodifiableList<T> downCast( UnmodifiableList<U> list )
	{
		@SuppressWarnings( "unchecked" ) UnmodifiableList<T> result = (UnmodifiableList<T>)list;
		return result;
	}

	static <E> UnmodifiableList<E> from( UnmodifiableCollection<E> collection )
	{
		E[] array = ConversionCollections.arrayOfObjectFromIterable( collection, collection.size() );
		return ConversionCollections.newArrayWrapper( array );
	}

	static <E> UnmodifiableList<E> from( Iterable<E> iterable )
	{
		E[] array = ConversionCollections.arrayOfObjectFromIterable( iterable, 0 );
		return ConversionCollections.newArrayWrapper( array );
	}

	static <E> UnmodifiableList<E> from( Iterable<E> iterable, int length )
	{
		E[] array = ConversionCollections.arrayOfObjectFromIterable( iterable, length );
		return ConversionCollections.newArrayWrapper( array );
	}

	static <E> UnmodifiableList<E> from( Iterable<E> iterable, EqualityComparator<? super E> equalityComparator )
	{
		E[] array = ConversionCollections.arrayOfObjectFromIterable( iterable, 0 );
		return ConversionCollections.newArrayWrapper( array, equalityComparator, true );
	}

	static <E> UnmodifiableList<E> from( Iterable<E> iterable, int length, EqualityComparator<? super E> equalityComparator )
	{
		E[] array = ConversionCollections.arrayOfObjectFromIterable( iterable, length );
		return ConversionCollections.newArrayWrapper( array, equalityComparator, true );
	}

	static <E> UnmodifiableList<E> from( UnmodifiableCollection<E> elements, EqualityComparator<? super E> equalityComparator )
	{
		if( elements.isEmpty() )
			return of();
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableList<E> mutableList = mutableCollections.newArrayList( elements.size(), equalityComparator );
			mutableList.addAll( elements );
			return mutableList;
		} );
	}

	static <E> UnmodifiableList<E> from( UnmodifiableList<E> elements, EqualityComparator<? super E> equalityComparator )
	{
		if( elements.isEmpty() )
			return of();
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableList<E> mutableList = mutableCollections.newArrayList( elements.size(), equalityComparator );
			mutableList.addAll( elements );
			return mutableList;
		} );
	}

	/**
	 * Gets the empty {@link UnmodifiableList}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableList}.
	 *
	 * @return the empty {@link UnmodifiableList}.
	 */
	static <E> UnmodifiableList<E> of() { return ImmutableCollections.emptyArrayList(); }

	static <E> UnmodifiableList<E> of( E e0 ) { return of0( e0 ); }
	static <E> UnmodifiableList<E> of( E e0, E e1 ) { return of0( e0, e1 ); }
	static <E> UnmodifiableList<E> of( E e0, E e1, E e2 ) { return of0( e0, e1, e2 ); }

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <E> UnmodifiableList<E> of( E... arrayOfElements )
	{
		return onArray( arrayOfElements );
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	private static <E> UnmodifiableList<E> of0( E... arrayOfElements )
	{
		return onArray( arrayOfElements ); //ConversionCollections.newArrayWrapper( arrayOfElements );
	}

	static <E> UnmodifiableList<E> onArray( E[] arrayOfElements, int offset, int length )
	{
		arrayOfElements = Kit.array.subArray( arrayOfElements, offset, length );
		return ConversionCollections.newArrayWrapper( arrayOfElements );
	}

	static <E> UnmodifiableList<E> onArray( E[] arrayOfElements )
	{
		return ConversionCollections.newArrayWrapper( arrayOfElements );
	}

	static UnmodifiableList<Integer> onArray( int... arrayOfInt )
	{
		Integer[] arrayOfInteger = ConversionCollections.newArray( arrayOfInt );
		return ConversionCollections.newArrayWrapper( arrayOfInteger );
	}

	private static <E, L extends UnmodifiableList<E>> L newUnmodifiableList( Function1<L,MutableCollections> factory, Procedure1<L> populator )
	{
		return Mortal.tryGetWith( ConcreteFreezableCoherence.create(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			L list = factory.invoke( mutableCollections );
			populator.invoke( list );
			return list;
		} );
	}

	static <E> UnmodifiableList<E> newList( Procedure1<MutableList<E>> populator )
	{
		return newUnmodifiableList( MutableCollections::newArrayList, populator );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of the element to return
	 *
	 * @return the element at the specified position in this list
	 *
	 * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
	 * @see java.util.List#get(int)
	 */
	E get( int index );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * {@link UnmodifiableList} decorator.
	 *
	 * @author michael.gr
	 */
	interface Decorator<E> extends Defaults<E>, UnmodifiableCollection.Decorator<E>
	{
		UnmodifiableList<E> getDecoratedUnmodifiableList();

		@Override default UnmodifiableCollection<E> getDecoratedUnmodifiableCollection()
		{
			return getDecoratedUnmodifiableList();
		}

		@Override default Optional<E> tryGet( E element )
		{
			return UnmodifiableList.Defaults.super.tryGet( element );
		}

		@Override default E get( int index )
		{
			UnmodifiableList<E> decoree = getDecoratedUnmodifiableList();
			return decoree.get( index );
		}

		@Override default UnmodifiableList<E> chain( UnmodifiableList<E> list )
		{
			UnmodifiableList<E> decoree = getDecoratedUnmodifiableList();
			return decoree.chain( list );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Searches the list for the given element, and Returns the index of the element or -1 if the list does not contain the element.
	 * <p>
	 * If end < start, a reverse search is performed.
	 *
	 * @param element the element to search for
	 * @param start   the index at which to start searching.
	 * @param end     the index at which to end searching. (Inclusive.)
	 *
	 * @return the index of the found element, or -1 if the element could not be found in the given range of indexes.
	 */
	int linearSearch( E element, int start, int end );

	/**
	 * Searches the list for a matching element, and Returns the index of the matching element or -1 if the list does not contain a matching element.
	 * <p>
	 * If end < start, a reverse search is performed.
	 *
	 * @param predicate the predicate to use for finding a matching element.
	 * @param start     the index at which to start searching.
	 * @param end       the index at which to end searching. (Inclusive.)
	 *
	 * @return the index of the matching element, or -1 if no matching element could be found in the given range of indexes.
	 */
	int linearSearch( Predicate<E> predicate, int start, int end );

	/**
	 * Searches the list for an element.
	 *
	 * @param elementToFind the element to search for.
	 * @param comparator    the {@link Comparator} to use for comparing elements.
	 *
	 * @return the index of the matching element, or the insertion point negated plus 1 if the list does not contain a matching element.
	 */
	int binarySearch( E elementToFind, Comparator<E> comparator );

	/**
	 * Finds a given element in this list.
	 *
	 * @return the index of the given element, or -1 if this list does not contain the element.
	 *
	 * @see java.util.List#indexOf(Object)
	 */
	int indexOf( E element );

	/**
	 * Finds a given element in this list, starting the search at a given position.
	 *
	 * @return the index of the given element, or -1 if this list does not contain the element.
	 *
	 * @see java.util.List#indexOf(Object)
	 */
	int indexOf( int start, E element );

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not contain the element.
	 *
	 * @see java.util.List#lastIndexOf(Object)
	 */
	int lastIndexOf( E element );

	/**
	 * Finds a matching element in this list.
	 *
	 * @return the index of the matching element, or -1 if this list does not contain a matching element.
	 */
	int indexOf( Predicate<E> predicate );

	/**
	 * Finds a matching element in this list, starting the search at a given position.
	 *
	 * @return the index of the matching element, or -1 if this list does not contain a matching element.
	 */
	int indexOf( int start, Predicate<E> predicate );

	/**
	 * Returns the index of the last match of the specified element in this list, or -1 if this list does not contain a matching element.
	 */
	int lastIndexOf( Predicate<E> predicate );

	/**
	 * Checks whether this {@link UnmodifiableList} is equal to the given {@link UnmodifiableList}.
	 *
	 * @param other the {@link UnmodifiableList} to compare against.
	 *
	 * @return {@code true} if the two {@link UnmodifiableList}s have the same contents.
	 */
	boolean equalsList( UnmodifiableList<? extends E> other );

	/**
	 * Fetches the last element in the {@link UnmodifiableList}.
	 * <p>
	 * The list must contain at least one element.
	 *
	 * @return the last element in the {@link UnmodifiableList}.
	 */
	E fetchLastElement();

	/**
	 * Fetches the last element in the {@link UnmodifiableList} or {@link Optional#empty()} if there are no elements.
	 *
	 * @return the last element in the {@link UnmodifiableList}.
	 */
	Optional<E> tryFetchLastElement();

	/**
	 * Provides a view of the list converted using a given {@link Function1}.
	 *
	 * @param converter the {@link Function1} invoked to convert each element.
	 * @param <T>       the type of the elements.
	 *
	 * @return a view of the list converted using the given {@link Function1}.
	 */
	@Override <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter );

	@Override <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, EqualityComparator<? super T> equalityComparator );

	@Override <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter );

	@Override <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter, EqualityComparator<? super T> equalityComparator );

	@Override <T> UnmodifiableList<T> mapWithIndex( TotalConverterWithIndex<? extends T,? super E> converter );

	/**
	 * Uses a given converter to convert each element to an {@link UnmodifiableList} of T and returns a single {@link UnmodifiableList} of T chaining together
	 * all the resulting lists.
	 */
	<T> UnmodifiableList<T> flatMapList( Function1<UnmodifiableList<T>,E> multiplier );

	/**
	 * Provides a view of this {@link UnmodifiableList} reversed.
	 *
	 * @return a view of this {@link UnmodifiableList}  reversed.
	 */
	UnmodifiableList<E> reverse();

	/**
	 * Returns a new {@link UnmodifiableList} representing all elements of this {@link UnmodifiableList} followed by all elements of a given {@link UnmodifiableList}.
	 *
	 * @param list the {@link UnmodifiableList} to add.
	 *
	 * @return a new {@link UnmodifiableList} representing all elements of this {@link UnmodifiableList} followed by all elements of the given {@link UnmodifiableList}.
	 */
	UnmodifiableList<E> chain( UnmodifiableList<E> list );

	@Override <T extends E> UnmodifiableList<T> upCast();

//	/**
//	 * Gets a new {@link UnmodifiableList} representing the elements converted by a given {@link Function1}.
//	 *
//	 * @param converter the {@link Function2} invoked to convert each element.
//	 *
//	 * @return a new {@link UnmodifiableList} representing the elements converted by the given {@link Function1}.
//	 */
//	@Override <T> UnmodifiableList<T> convertedWithIndex( Function2<? extends T,? super E,Integer> converter );
//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Compares this collection against a given collection performing a shallow comparison (reference comparison).
	 *
	 * @param other the collection to compare against.
	 *
	 * @return {@code true} if all elements are the same by reference; {@code false} if one or more of the given elements did not already exist.
	 */
	boolean shallowEquals( UnmodifiableList<? super E> other );

	/**
	 * Gets the last element of the list.  (The list must contain at least one element.)
	 *
	 * @return the last element of the list.
	 */
	E getLast();

	/**
	 * Obtains a list which is a "view" into a range of items in this list.
	 *
	 * @return a new {@link UnmodifiableList}.
	 */
	UnmodifiableList<E> subList( int offset, int length );

	/**
	 * Obtains a list which is a "view" into a range of items in this list.
	 *
	 * @return a new {@link UnmodifiableList}.
	 */
	UnmodifiableList<E> subList( int offset );

	/**
	 * Default methods for {@link UnmodifiableList}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<E> extends UnmodifiableList<E>, UnmodifiableCollection.Defaults<E>
	{
		@Override default int linearSearch( E element, int start, int end )
		{
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			return linearSearch( e -> equalityComparator.equals( e, element ), start, end );
		}

		@Override default int linearSearch( Predicate<E> predicate, int start, int end )
		{
			int step = start < end ? 1 : -1;
			for( int i = start; ; i += step )
			{
				E element = get( i );
				if( predicate.test( element ) )
					return i;
				if( i == end )
					break;
			}
			return -1;
		}

		@Override default int binarySearch( E elementToFind, Comparator<E> comparator )
		{
			int low = 0;
			int high = size() - 1;
			while( low <= high )
			{
				int mid = (low + high) >>> 1;
				E middleElement = get( mid );
				int c = comparator.compare( elementToFind, middleElement );
				if( c < 0 )
					high = mid - 1;
				else if( c > 0 )
					low = mid + 1;
				else
					return mid; // key found
			}
			return -(low + 1); // key not found.
		}

		@Override default int indexOf( E element )
		{
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			return indexOf( e -> equalityComparator.equals( e, element ) );
		}

		@Override default int indexOf( int start, E element )
		{
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			return indexOf( start, e -> equalityComparator.equals( e, element ) );
		}

		@Override default int lastIndexOf( E element )
		{
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			return lastIndexOf( e -> equalityComparator.equals( e, element ) );
		}

		@Override default int indexOf( Predicate<E> predicate )
		{
			int length = size();
			if( length == 0 )
				return -1;
			return linearSearch( predicate, 0, length - 1 );
		}

		@Override default int indexOf( int start, Predicate<E> predicate )
		{
			assert start < size();
			int length = size();
			if( length == 0 )
				return -1;
			return linearSearch( predicate, start, length - 1 );
		}

		@Override default int lastIndexOf( Predicate<E> predicate )
		{
			int length = size();
			if( length == 0 )
				return -1;
			return linearSearch( predicate, length - 1, 0 );
		}

		@Override default Optional<E> tryGet( E element )
		{
			int index = indexOf( element );
			if( index == -1 )
				return Optional.empty();
			return Optional.of( get( index ) );
		}

		@Override default E fetchFirstElement()
		{
			assert nonEmpty();
			return get( 0 );
		}

		@Override default E fetchLastElement()
		{
			assert nonEmpty();
			return get( size() - 1 );
		}

		@Override default Optional<E> tryFetchLastElement()
		{
			if( isEmpty() )
				return Optional.empty();
			return Optional.of( fetchLastElement() );
		}

		@Override default <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter )
		{
			return map( converter, t -> Kit.fail(), DefaultEqualityComparator.getInstance() );
		}

		@Override default <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, EqualityComparator<? super T> equalityComparator )
		{
			return map( converter, t -> Kit.fail(), equalityComparator );
		}

		@Override default <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter )
		{
			return map( converter, reverter, new PartiallyConvertingEqualityComparator<>( reverter ) );
		}

		@Override default <T> UnmodifiableList<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
		{
			return ConversionCollections.newConvertingList( this, ( i, e ) -> converter.invoke( e ), reverter, equalityComparator );
		}

		@Override default <T> UnmodifiableList<T> mapWithIndex( TotalConverterWithIndex<? extends T,? super E> converter )
		{
			return ConversionCollections.newConvertingList( this, converter, t -> Kit.fail(), DefaultEqualityComparator.getInstance() );
		}

		@Override default <T> UnmodifiableList<T> flatMapList( Function1<UnmodifiableList<T>,E> multiplier )
		{
			return new FlatteningList<>( this, multiplier );
		}

		@Override default UnmodifiableList<E> reverse()
		{
			return ConversionCollections.newReversingList( this );
		}

		@Override default UnmodifiableList<E> chain( UnmodifiableList<E> list )
		{
			return ConversionCollections.newChainingListOf( this, list );
		}

		@Override default <T extends E> UnmodifiableList<T> upCast()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableList<T> result = (UnmodifiableList<T>)this;
			return result;
		}

		@Override default boolean shallowEquals( UnmodifiableList<? super E> other )
		{
			int length = size();
			if( length != other.size() )
				return false;
			for( int i = 0; i < length; i++ )
				if( get( i ) != other.get( i ) )
					return false;
			return true;
		}

		@Override default E getLast()
		{
			int length = size();
			assert length > 0;
			return get( length - 1 );
		}

		@Override default UnmodifiableList<E> subList( int startOffset, int endOffset )
		{
			return ConversionCollections.newSubList( this, startOffset, endOffset );
		}

		@Override default UnmodifiableList<E> subList( int offset )
		{
			return subList( offset, size() );
		}

		@Override default UnmodifiableCollection<E> toCollection()
		{
			return toList();
		}

		@Override default UnmodifiableList<E> toList()
		{
			if( isEmpty() )
				return UnmodifiableList.of();
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			return UnmodifiableList.from( this, equalityComparator );
		}

		/**
		 * Checks whether this {@link UnmodifiableList} is equal to the given {@link UnmodifiableList}.
		 *
		 * @param other the {@link UnmodifiableList} to compare against.
		 *
		 * @return {@code true} if the two {@link UnmodifiableList}s have the same contents.
		 */
		@Override default boolean equalsList( UnmodifiableList<? extends E> other )
		{
			if( this == other )
				return true;
			int length = size();
			if( length != other.size() )
				return false;
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			for( int i = 0; i < length; i++ )
			{
				E element = get( i );
				E otherElement = other.get( i );
				if( !equalityComparator.equals( element, otherElement ) )
					return false;
			}
			return true;
		}
	}

	/**
	 * Canary class.
	 * <p>
	 * This is a concrete class to make sure that if there are problems with the interface making it impossible to inherit from, they will be caught by the compiler at the
	 * earliest point possible, and not when compiling some derived class.
	 */
	@ExcludeFromJacocoGeneratedReport
	@SuppressWarnings( "unused" )
	final class Canary<E> implements Decorator<E>
	{
		@Override public UnmodifiableList<E> getDecoratedUnmodifiableList()
		{
			return this;
		}
	}
}
