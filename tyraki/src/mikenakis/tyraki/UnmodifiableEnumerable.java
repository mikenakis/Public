package mikenakis.tyraki;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function0;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.functional.Function2;
import mikenakis.kit.GenericException;
import mikenakis.kit.functional.Procedure1;
import mikenakis.tyraki.conversion.ConversionCollections;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Unmodifiable Enumerable.
 *
 * @author michael.gr
 */
public interface UnmodifiableEnumerable<E> extends Iterable<E>, Comparable<UnmodifiableList<E>>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param enumerable the {@link UnmodifiableEnumerable} to down-cast.
	 * @param <T>        the super type
	 * @param <U>        the type of the {@link UnmodifiableEnumerable}.
	 *
	 * @return the same {@link UnmodifiableEnumerable} cast to an ancestral type.
	 */
	static <T, U extends T> UnmodifiableEnumerable<T> downCast( UnmodifiableEnumerable<U> enumerable )
	{
		@SuppressWarnings( "unchecked" ) UnmodifiableEnumerable<T> result = (UnmodifiableEnumerable<T>)enumerable;
		return result;
	}

	static UnmodifiableEnumerable<Integer> ofInt( int count )
	{
		return ofInt( 0, count, 1 );
	}

	static UnmodifiableEnumerable<Integer> ofInt( int startInclusive, int endExclusive, int step )
	{
		return of( startInclusive, i -> (i += step) < endExclusive ? Optional.of( i ) : Optional.empty() );
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <T> UnmodifiableEnumerable<T> of( T e0, T... arrayOfElements )
	{
		return of( e0 ).chained( ConversionCollections.newArrayWrapper( arrayOfElements ) );
	}

	static <T> UnmodifiableEnumerable<T> of( T firstElement, Function1<Optional<T>,T> nextElementProducer )
	{
		return ConversionCollections.newEnumerable( firstElement, nextElementProducer );
	}

	static <T> UnmodifiableEnumerable<T> of()
	{
		return UnmodifiableCollection.of();
	}

	static <T> UnmodifiableEnumerable<T> of( T element )
	{
		return ConversionCollections.newSingleElementEnumerable( element );
	}

	static <T> UnmodifiableEnumerable<T> ofOptional( Optional<? extends T> element )
	{
		if( element.isEmpty() )
			return of();
		return ConversionCollections.newSingleElementEnumerable( element.get() );
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) static <T> UnmodifiableEnumerable<T> ofChained( UnmodifiableEnumerable<T>... enumerables )
	{
		UnmodifiableList<UnmodifiableEnumerable<T>> list = UnmodifiableList.onArray( enumerables );
		return ofChained( list );
	}

	static <T> UnmodifiableEnumerable<T> ofChained( UnmodifiableCollection<UnmodifiableEnumerable<T>> enumerables )
	{
		return ConversionCollections.newChainingEnumerable( enumerables );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean isFrozenAssertion();

	/**
	 * Creates a new {@link UnmodifiableEnumerator} to enumerate elements.
	 *
	 * @return an {@link UnmodifiableEnumerator}.
	 */
	UnmodifiableEnumerator<E> newUnmodifiableEnumerator();

	/**
	 * Gets the modification count of this collection.
	 *
	 * @return the modification count.
	 */
	int getModificationCount();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Collects all elements in a list.
	 */
	UnmodifiableList<E> toList();

	/**
	 * Computes the hash code of all elements in this unmodifiable enumerable.
	 */
	int calculateHashCode();

	/**
	 * Gets the one and only element of this {@link UnmodifiableEnumerable}.
	 *
	 * @return the first element of the {@link UnmodifiableEnumerable}.
	 *
	 * @throws Error if the {@link UnmodifiableEnumerable} yields no elements, or if the {@link UnmodifiableEnumerable} yields more than one element.
	 */
	E fetchSingleElement();

	/**
	 * Gets the one and only element, or {@link Optional#empty()} if the {@link UnmodifiableEnumerable} is empty.
	 *
	 * @return the first and only element, or {@link Optional#empty()}} if the {@link UnmodifiableEnumerable} is empty.
	 *
	 * @throws Error if the {@link UnmodifiableEnumerable} yields more than one element.
	 */
	Optional<E> tryFetchSingleElement();

	/**
	 * Fetches the first element of an {@link UnmodifiableEnumerable}.
	 *
	 * @return the first element of the {@link UnmodifiableEnumerable}.
	 *
	 * @throws Error if the {@link UnmodifiableEnumerable} yields no elements.
	 */
	E fetchFirstElement();

	/**
	 * Fetches the first element of an {@link UnmodifiableEnumerable}, or {@link Optional#empty()}} if the {@link UnmodifiableEnumerable} is empty.
	 *
	 * @return the first element of the {@link UnmodifiableEnumerable}.
	 */
	Optional<E> tryFetchFirstElement();

	/**
	 * Executes a given method if this {@link UnmodifiableEnumerable} is non-empty.
	 */
	void ifNonEmpty( Procedure1<UnmodifiableEnumerable<E>> handler );

	/**
	 * Fetches the Nth element, or {@link Optional#empty()} if the {@link UnmodifiableEnumerable} is empty.
	 *
	 * @param n the number of the element to fetch.
	 *
	 * @return the n-th element, or {@link Optional#empty()} if the {@link UnmodifiableEnumerable} is empty.
	 */
	Optional<E> tryFetchNthElement( int n );

	/**
	 * Fetches the Nth element.
	 *
	 * @param n the number of the element to fetch.
	 *
	 * @return the n-th element.
	 */
	E fetchNthElement( int n );

	/**
	 * Counts the elements of this {@link UnmodifiableEnumerable}, possibly by enumerating them.
	 *
	 * @return the number of elements in this {@link UnmodifiableEnumerable}.
	 */
	int countElements();

	/**
	 * Determines whether this {@link UnmodifiableEnumerable} is empty, possibly by starting an enumeration.
	 *
	 * @return {code true} if the enumerable is empty; false otherwise.
	 */
	boolean isEmpty();

	/**
	 * Gets a new {@link UnmodifiableEnumerable} representing the elements filtered by a given {@link Predicate}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element to decide whether to keep it or skip it.
	 *
	 * @return a new {@link UnmodifiableEnumerable} representing the elements filtered by the given {@link Predicate}.
	 */
	UnmodifiableEnumerable<E> filter( Predicate<? super E> predicate );

	<T extends E> UnmodifiableEnumerable<T> filter( Class<T> elementClass );

	/**
	 * Gets a new {@link UnmodifiableEnumerable} representing the elements converted by a given {@link Function1}.
	 *
	 * @param converter the {@link Function1} invoked to convert each element.
	 *
	 * @return a new {@link UnmodifiableEnumerable} representing the elements converted by the given {@link Function1}.
	 */
	<T> UnmodifiableEnumerable<T> map( Function1<? extends T,? super E> converter );

	/**
	 * Uses a given converter to convert each element to an {@link UnmodifiableEnumerable} of T and returns a single enumerable of T chaining together all
	 * the resulting enumerables.
	 */
	<T> UnmodifiableEnumerable<T> flatMap( Function1<UnmodifiableEnumerable<T>,E> multiplier );

	/**
	 * Creates a new {@link UnmodifiableEnumerable} representing the elements of this {@link UnmodifiableEnumerable} converted and filtered using the given {@link Function1}.
	 * <p>
	 * The converter must return {@link Optional#empty()} if the item is to be filtered out; non-empty otherwise.
	 *
	 * @param converter the {@link Function1} to use.
	 * @param <T>       the type of items to convert to.
	 *
	 * @return a new {@link UnmodifiableEnumerable} representing the elements of this {@link UnmodifiableEnumerable} converted and filtered using the given {@link Function1}.
	 */
	<T> UnmodifiableEnumerable<T> flatMapOptionals( Function1<Optional<T>,E> converter );

	/**
	 * Gets a new {@link UnmodifiableEnumerable} representing the elements converted by a given {@link Function1}.
	 *
	 * @param converter the {@link Function2} invoked to convert each element.
	 *
	 * @return a new {@link UnmodifiableEnumerable} representing the elements converted by the given {@link Function1}.
	 */
	<T> UnmodifiableEnumerable<T> mapWithIndex( TotalConverterWithIndex<? extends T,? super E> converter );

	/**
	 * Walks the {@link UnmodifiableEnumerable} for as long as the supplied function returns {@link Optional#empty()}.
	 *
	 * @param function the {@link Function0} invoked for each element.
	 *
	 * @return An {@link Optional} containing the value returned by the supplied {@link Function0} if the walk was stopped;  {@link Optional#empty()} if the enumerable was walked to the end.
	 */
	<T> Optional<T> walk( Function0<T> function );

	/**
	 * Checks whether a given {@link Predicate} returns {@code true} for all elements in the {@link UnmodifiableEnumerable}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element.
	 *
	 * @return {@code true} if the predicate returned {@code true} for all elements.
	 */
	boolean trueForAll( Predicate<? super E> predicate );

	/**
	 * Checks whether a given {@link Predicate} returns {@code false} for all elements in the {@link UnmodifiableEnumerable}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element.
	 *
	 * @return {@code true} if the predicate returned {@code false} for all elements.
	 */
	boolean falseForAll( Predicate<? super E> predicate );

	/**
	 * Checks whether a given {@link Predicate} returns {@code true} for all elements in the {@link UnmodifiableEnumerable}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element.
	 *
	 * @return {@code true} if the predicate returned {@code true} for all elements.
	 */
	boolean assertForAll( Predicate<? super E> predicate );

	/**
	 * Checks whether a given {@link Predicate} returns {@code true} for any elements in the {@link UnmodifiableEnumerable}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element.
	 *
	 * @return {@code true} if the predicate returned {@code true} for any element.
	 */
	boolean trueForAny( Predicate<? super E> predicate );

	/**
	 * Checks whether a given {@link Predicate} returns {@code false} for any elements in the {@link UnmodifiableEnumerable}.
	 *
	 * @param predicate the {@link Predicate} invoked for each element.
	 *
	 * @return {@code true} if the predicate returned {@code false} for any element.
	 */
	boolean falseForAny( Predicate<? super E> predicate );

	/**
	 * Up-casts this {@link UnmodifiableEnumerable} to the given type.
	 */
	<T extends E> UnmodifiableEnumerable<T> upCast();

	/**
	 * casts (unchecked) this {@link UnmodifiableEnumerable} to the given type.
	 */
	<T> UnmodifiableEnumerable<T> uncheckedCast();

	/**
	 * Obtains a new {@link UnmodifiableEnumerable} representing the elements of this {@link UnmodifiableEnumerable} followed by the elements of a given {@link
	 * UnmodifiableEnumerable}.
	 *
	 * @param other the {@link UnmodifiableEnumerable} to chain.
	 *
	 * @return a new {@link UnmodifiableEnumerable} representing the elements of this {@link UnmodifiableEnumerable} followed by the elements of a given {@link
	 *    UnmodifiableEnumerable}.
	 */
	UnmodifiableEnumerable<E> chained( UnmodifiableEnumerable<E> other );

	/**
	 * Converts this {@link UnmodifiableEnumerable} to an array of Object.
	 *
	 * @return an array of Object.
	 */
	E[] toArrayOfObject();

	/**
	 * Appends the string representation this enumerable (using the given delimiter) to a {@link StringBuilder}.
	 *
	 * @param stringBuilder the {@link StringBuilder} to append to.
	 * @param delimiter     the delimiter to use.
	 */
	void appendToStringBuilder( StringBuilder stringBuilder, String delimiter );

	/**
	 * Appends the string representation this enumerable (using the given delimiter) to a {@link StringBuilder}.
	 *
	 * @param stringBuilder         the {@link StringBuilder} to append to.
	 * @param delimiter             the delimiter to use.
	 * @param stringBuilderAppender the {@link StringBuilderAppender} to use.
	 */
	void appendToStringBuilder( StringBuilder stringBuilder, String delimiter, StringBuilderAppender<E> stringBuilderAppender );

	/**
	 * Appends the string representation this enumerable (using the given prefix, delimiter and suffix) to a {@link StringBuilder}.
	 *
	 * @param stringBuilder the {@link StringBuilder} to append to.
	 * @param prefix        the prefix to use.
	 * @param delimiter     the delimiter to use.
	 * @param suffix        the suffix to use.
	 */
	void appendToStringBuilder( StringBuilder stringBuilder, String prefix, String delimiter, String suffix );

	interface StringBuilderAppender<T>
	{
		void appendToStringBuilder( T object, StringBuilder stringBuilder );
	}

	/**
	 * Appends the string representation this enumerable (using the given prefix, delimiter and suffix) to a {@link StringBuilder}.
	 *
	 * @param stringBuilder         the {@link StringBuilder} to append to.
	 * @param prefix                the prefix to use.
	 * @param delimiter             the delimiter to use.
	 * @param suffix                the suffix to use.
	 * @param stringBuilderAppender the {@link StringBuilderAppender} to use.
	 */
	void appendToStringBuilder( StringBuilder stringBuilder, String prefix, String delimiter, String suffix, StringBuilderAppender<E> stringBuilderAppender );

	/**
	 * Converts this enumerable to a string using the given delimiter.
	 *
	 * @param delimiter the delimiter to use.
	 *
	 * @return a string representation of this enumerable.
	 */
	String makeString( String delimiter );

	String makeString( char delimiter );

	/**
	 * Converts this enumerable to a string using the given prefix, delimiter and suffix.
	 *
	 * @param prefix    the prefix to use.
	 * @param delimiter the delimiter to use.
	 * @param suffix    the suffix to use.
	 *
	 * @return a string representation of this enumerable.
	 */
	String makeString( String prefix, String delimiter, String suffix );

	/**
	 * Converts this enumerable to a string using the given delimiter and {@link StringBuilderAppender}.
	 *
	 * @param delimiter             the delimiter to use.
	 * @param stringBuilderAppender the {@link StringBuilderAppender} to use.
	 *
	 * @return a string representation of this enumerable.
	 */
	String makeString( String delimiter, StringBuilderAppender<E> stringBuilderAppender );

	/**
	 * Converts this enumerable to a string using the given prefix, delimiter, suffix, and {@link StringBuilderAppender}.
	 *
	 * @param prefix                the prefix to use.
	 * @param delimiter             the delimiter to use.
	 * @param suffix                the suffix to use.
	 * @param stringBuilderAppender the {@link StringBuilderAppender} to use.
	 *
	 * @return a string representation of this enumerable.
	 */
	String makeString( String prefix, String delimiter, String suffix, StringBuilderAppender<E> stringBuilderAppender );

	boolean equalsEnumerable( UnmodifiableEnumerable<E> other );

	boolean equalsEnumerable( UnmodifiableEnumerable<E> other, EqualityComparator<E> equalityComparator );

	@Deprecated @Override boolean equals( Object other ); //Java blooper: Java prevents us from declaring a default method overriding equals().

	<T> T reduce( T start, Function2<T,T,E> combiner );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for {@link UnmodifiableEnumerable}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<E> extends UnmodifiableEnumerable<E>
	{
		@Override default UnmodifiableList<E> toList()
		{
			E[] elements = toArrayOfObject();
			return ConversionCollections.newArrayWrapper( elements, true );
		}

		@Override default int calculateHashCode()
		{
			int hash = 17;
			for( E element : this )
				hash = 31 * hash + Objects.hashCode( element );
			return hash;
		}

		@Override default E fetchSingleElement()
		{
			Optional<E> result = tryFetchSingleElement();
			assert result.isPresent() : new GenericException( "no element found!" );
			return result.get();
		}

		@Override default Optional<E> tryFetchSingleElement()
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			if( enumerator.isFinished() )
				return Optional.empty();
			E result = enumerator.getCurrent();
			assert enumerator.moveNext().isFinished() : new GenericException( "more elements present!" );
			return Optional.of( result );
		}

		@Override default E fetchFirstElement()
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			assert !enumerator.isFinished() : new NoSuchElementException();
			return enumerator.getCurrent();
		}

		@Override default Optional<E> tryFetchFirstElement()
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			if( enumerator.isFinished() )
				return Optional.empty();
			return Optional.of( enumerator.getCurrent() );
		}

		@Override default void ifNonEmpty( Procedure1<UnmodifiableEnumerable<E>> handler )
		{
			if( !isEmpty() )
				handler.invoke( this );
		}

		@Override default Optional<E> tryFetchNthElement( int n )
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			for( int i = 0; i < n; i++ )
			{
				if( enumerator.isFinished() )
					return Optional.empty();
				enumerator.moveNext();
			}
			if( enumerator.isFinished() )
				return Optional.empty();
			return Optional.of( enumerator.getCurrent() );
		}

		@Override default E fetchNthElement( int n )
		{
			Optional<E> result = tryFetchNthElement( n );
			assert result.isPresent();
			return result.get();
		}

		@Override default int countElements()
		{
			int count = 0;
			for( E ignore : this )
				count++;
			return count;
		}

		@Override default boolean isEmpty()
		{
			for( E ignore : this )
				return false;
			return true;
		}

		@Override default Iterator<E> iterator()
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			return new IteratorOnUnmodifiableEnumerator<>( enumerator );
		}

		@Override default UnmodifiableEnumerable<E> filter( Predicate<? super E> predicate )
		{
			return ConversionCollections.newFilteringEnumerable( this, predicate );
		}

		@Override default <T extends E> UnmodifiableEnumerable<T> filter( Class<T> elementClass )
		{
			return flatMapOptionals( element -> Kit.tryAs( elementClass, element ) );
		}

		@Override default <T> UnmodifiableEnumerable<T> map( Function1<? extends T,? super E> converter )
		{
			return ConversionCollections.newConvertingUnmodifiableEnumerable( this, ( i, e ) -> converter.invoke( e ) );
		}

		@Override default <T> UnmodifiableEnumerable<T> mapWithIndex( TotalConverterWithIndex<? extends T,? super E> converter )
		{
			return ConversionCollections.newConvertingUnmodifiableEnumerable( this, converter );
		}

		@Override default <T> UnmodifiableEnumerable<T> flatMapOptionals( Function1<Optional<T>,E> converter )
		{
			return ConversionCollections.newConvertingUnmodifiableEnumerable( this, ( i, e ) -> converter.invoke( e ) ).filter( Optional::isPresent ).map( Optional::orElseThrow );
		}

		@Override default <T> Optional<T> walk( Function0<T> function )
		{
			assert false; //not implemented yet
			return Optional.empty();
		}

		@Override default boolean trueForAll( Predicate<? super E> predicate )
		{
			for( E element : this )
				if( !predicate.test( element ) )
					return false;
			return true;
		}

		@Override default boolean falseForAll( Predicate<? super E> predicate )
		{
			for( E element : this )
				if( predicate.test( element ) )
					return false;
			return true;
		}

		@Override default boolean assertForAll( Predicate<? super E> predicate )
		{
			for( E element : this )
				assert predicate.test( element );
			return true;
		}

		@Override default boolean trueForAny( Predicate<? super E> predicate )
		{
			for( E element : this )
				if( predicate.test( element ) )
					return true;
			return false;
		}

		@Override default boolean falseForAny( Predicate<? super E> predicate )
		{
			for( E element : this )
				if( !predicate.test( element ) )
					return true;
			return false;
		}

		@Override default <T extends E> UnmodifiableEnumerable<T> upCast()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableEnumerable<T> result = (UnmodifiableEnumerable<T>)this;
			return result;
		}

		@Override default <T> UnmodifiableEnumerable<T> uncheckedCast()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableEnumerable<T> result = (UnmodifiableEnumerable<T>)this;
			return result;
		}

		@Override default UnmodifiableEnumerable<E> chained( UnmodifiableEnumerable<E> other )
		{
			return ConversionCollections.newChainingEnumerableOf( this, other );
		}

		@Override default E[] toArrayOfObject()
		{
			return ConversionCollections.arrayOfObjectFromIterable( this, 0 );
		}

		@Override default int compareTo( UnmodifiableList<E> other )
		{
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			UnmodifiableEnumerator<E> otherEnumerator = other.newUnmodifiableEnumerator();
			for( ; ; )
			{
				if( enumerator.isFinished() )
					return otherEnumerator.isFinished() ? 0 : 1;
				if( otherEnumerator.isFinished() )
					return -1;
				E a = enumerator.getCurrent();
				E b = otherEnumerator.getCurrent();
				int d = compare( a, b );
				if( d != 0 )
					return d;
			}
		}

		@SuppressWarnings( "unchecked" ) private static <E, C extends Comparable<C>> int compare( E a, E b )
		{
			if( a == null )
				return b == null ? 0 : -1;
			if( b == null )
				return 1;
			return ((Comparable<C>)a).compareTo( (C)b );
		}

		@Override default void appendToStringBuilder( StringBuilder stringBuilder, String delimiter )
		{
			appendToStringBuilder( stringBuilder, "", delimiter, "", UnmodifiableEnumerable::defaultStringBuilderAppender );
		}

		@Override default void appendToStringBuilder( StringBuilder stringBuilder, String delimiter, StringBuilderAppender<E> stringBuilderAppender )
		{
			appendToStringBuilder( stringBuilder, "", delimiter, "", stringBuilderAppender );
		}

		@Override default void appendToStringBuilder( StringBuilder stringBuilder, String prefix, String delimiter, String suffix )
		{
			appendToStringBuilder( stringBuilder, prefix, delimiter, suffix, UnmodifiableEnumerable::defaultStringBuilderAppender );
		}

		@Override default void appendToStringBuilder( StringBuilder stringBuilder, String prefix, String delimiter, String suffix, StringBuilderAppender<E> stringBuilderAppender )
		{
			boolean first = true;
			for( E element : this )
			{
				if( first )
				{
					stringBuilder.append( prefix );
					first = false;
				}
				else
					stringBuilder.append( delimiter );
				stringBuilderAppender.appendToStringBuilder( element, stringBuilder );
			}
			if( !first )
				stringBuilder.append( suffix );
		}

		@Override default String makeString( String delimiter )
		{
			return makeString( "", delimiter, "" );
		}

		@Override default String makeString( char delimiter )
		{
			return makeString( String.valueOf( delimiter ) );
		}

		@Override default String makeString( String prefix, String delimiter, String suffix )
		{
			return makeString( prefix, delimiter, suffix, UnmodifiableEnumerable::defaultStringBuilderAppender );
		}

		@Override default String makeString( String delimiter, StringBuilderAppender<E> stringBuilderAppender )
		{
			return makeString( "", delimiter, "", stringBuilderAppender );
		}

		@Override default String makeString( String prefix, String delimiter, String suffix, StringBuilderAppender<E> stringBuilderAppender )
		{
			StringBuilder stringBuilder = new StringBuilder();
			appendToStringBuilder( stringBuilder, prefix, delimiter, suffix, stringBuilderAppender );
			return stringBuilder.toString();
		}

		@Override default boolean equalsEnumerable( UnmodifiableEnumerable<E> other )
		{
			return equalsEnumerable( other, DefaultEqualityComparator.getInstance() );
		}

		@Override default boolean equalsEnumerable( UnmodifiableEnumerable<E> other, EqualityComparator<E> equalityComparator )
		{
			if( other == this )
				return true;
			UnmodifiableEnumerator<E> enumerator = newUnmodifiableEnumerator();
			UnmodifiableEnumerator<? extends E> otherEnumerator = other.newUnmodifiableEnumerator();
			for( ; ; )
			{
				if( enumerator.isFinished() )
					return otherEnumerator.isFinished();
				if( otherEnumerator.isFinished() )
					return false;
				E value = enumerator.getCurrent();
				E otherValue = otherEnumerator.getCurrent();
				if( !equalityComparator.equals( value, otherValue ) )
					return false;
				enumerator.moveNext();
				otherEnumerator.moveNext();
			}
		}

		@Override default <T> UnmodifiableEnumerable<T> flatMap( Function1<UnmodifiableEnumerable<T>,E> multiplier )
		{
			return new FlatteningEnumerable<>( this, multiplier );
		}

		@Override default <T> T reduce( T start, Function2<T,T,E> combiner )
		{
			for( E element : this )
				start = combiner.invoke( start, element );
			return start;
		}
	}

	static <E> void defaultStringBuilderAppender( E element, StringBuilder stringBuilder )
	{
		stringBuilder.append( element );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Decorator<E> extends Defaults<E>
	{
		UnmodifiableEnumerable<E> getDecoratedUnmodifiableEnumerable();

		@Override default boolean isFrozenAssertion()
		{
			UnmodifiableEnumerable<E> decoree = getDecoratedUnmodifiableEnumerable();
			return decoree.isFrozenAssertion();
		}

		@Override default UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
		{
			UnmodifiableEnumerable<E> decoree = getDecoratedUnmodifiableEnumerable();
			return decoree.newUnmodifiableEnumerator();
		}

		@Override default int getModificationCount()
		{
			UnmodifiableEnumerable<E> decoree = getDecoratedUnmodifiableEnumerable();
			return decoree.getModificationCount();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
		@Override public UnmodifiableEnumerable<E> getDecoratedUnmodifiableEnumerable()
		{
			return this;
		}
	}
}
