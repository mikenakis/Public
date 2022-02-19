package mikenakis.tyraki;

import mikenakis.kit.DefaultComparator;
import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.conversion.ConversionCollections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Unmodifiable Collection.
 * <p>
 * The collection is not necessarily immutable: it may be modified by mechanisms outside the beholder's scope and beyond the beholder's control.  (Thus, the
 * fact that this interface is extended by the interface which represents a modifiable collection does not violate Liskov's "History Constraint".)
 * <p>
 * I wish it was possible to simply call this interface 'Collection' and call the mutable version 'MutableCollection', but I can't, due to the lack of
 * namespaces in java which essentially prohibits us from giving a type the same name as an existing, widely used type.
 *
 * @author michael.gr
 */
public interface UnmodifiableCollection<E> extends UnmodifiableEnumerable<E>
{
	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	static <T> UnmodifiableCollection<T> ofChained( UnmodifiableCollection<T>... arrayOfCollections )
	{
		UnmodifiableCollection<UnmodifiableCollection<T>> collections = UnmodifiableList.onArray( arrayOfCollections );
		return ofChained( collections );
	}

	static <T> UnmodifiableCollection<T> ofChained( UnmodifiableCollection<UnmodifiableCollection<T>> collections )
	{
		return ConversionCollections.newChainingCollection( collections );
	}

	/**
	 * Returns an {@link UnmodifiableCollection} of down-casted elements.
	 *
	 * @param collection the {@link UnmodifiableCollection} to down-cast.
	 * @param <T>        the type of the ancestor.
	 *
	 * @return the same {@link UnmodifiableCollection} cast to an ancestral type.
	 */
	static <T> UnmodifiableCollection<T> downCast( UnmodifiableCollection<? extends T> collection )
	{
		@SuppressWarnings( "unchecked" )
		UnmodifiableCollection<T> result = (UnmodifiableCollection<T>)collection;
		return result;
	}

	/**
	 * Gets the empty {@link UnmodifiableCollection}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableList}.
	 *
	 * @return the empty {@link UnmodifiableList}.
	 */
	static <E> UnmodifiableCollection<E> of()
	{
		return UnmodifiableList.of();
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <E> UnmodifiableCollection<E> of( E... arrayOfElements )
	{
		return UnmodifiableList.onArray( arrayOfElements );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Obtains an item in this {@link UnmodifiableCollection}.
	 *
	 * @param element the item to find.
	 *
	 * @return a matching element from the {@link UnmodifiableCollection} or {@link Optional#empty()} if no such item exists.
	 *
	 * Note: if items have reference semantics, then any returned item will be identical to the one passed as a parameter.
	 * However, if items have object semantics, the returned item may be a different instance from the parameter, though comparing equally to it.
	 */
	Optional<E> tryGet( E element );

	/**
	 * Checks whether this {@link UnmodifiableCollection} contains a certain item.
	 *
	 * @param element the item to check for containment.
	 *
	 * @return {@code true} if the {@link UnmodifiableCollection} contains the given item; {@code false} otherwise.
	 *
	 * @see Collection#contains(Object)
	 */
	boolean contains( E element );

	/**
	 * Gets the {@link EqualityComparator} used by this {@link UnmodifiableCollection}.
	 *
	 * @return the {@link EqualityComparator} to use.
	 */
	EqualityComparator<? super E> getEqualityComparator();

	/**
	 * Gets the number of elements in this {@link UnmodifiableCollection}.
	 *
	 * @return the number of elements.
	 */
	int size();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether this {@link UnmodifiableCollection} is empty.
	 *
	 * @return {@code true} if this {@link UnmodifiableCollection} is empty; {@code false} otherwise.
	 */
	@Override boolean isEmpty();

	/**
	 * Checks whether this {@link UnmodifiableCollection} is non-empty.
	 *
	 * @return {@code true} if this {@link UnmodifiableCollection} is non-empty; {@code false} otherwise.
	 */
	boolean nonEmpty();

	/**
	 * Checks whether this {@link UnmodifiableCollection} contains any of the items in the given {@link UnmodifiableEnumerable}.
	 *
	 * @param other an {@link UnmodifiableEnumerable} with items to check for containment.
	 *
	 * @return {@code true} if this {@link UnmodifiableCollection} contains any of the items in the given {@link UnmodifiableEnumerable}; {@code false} otherwise.
	 */
	boolean containsAny( UnmodifiableEnumerable<? extends E> other );

	/**
	 * Checks whether this {@link UnmodifiableCollection} contains all the items in the given {@link UnmodifiableEnumerable}.
	 *
	 * @param other an {@link UnmodifiableEnumerable} with items to check for containment.
	 *
	 * @return {@code true} if this {@link UnmodifiableCollection} contains all the items in the given {@link UnmodifiableEnumerable}; {@code false} otherwise.
	 */
	boolean containsAll( UnmodifiableEnumerable<? extends E> other );

	/**
	 * Converts this {@link UnmodifiableCollection} to an array.
	 *
	 * @param arrayFactory a {@link Function1} to create an array of the required number of elements.
	 *
	 * @return an array of elements.
	 */
	E[] toArray( Function1<E[],Integer> arrayFactory );

	/**
	 * Checks whether this {@link UnmodifiableCollection} is equal to the given {@link UnmodifiableCollection}.
	 *
	 * @param other the {@link UnmodifiableCollection} to compare against.
	 *
	 * @return {@code true} if the two {@link UnmodifiableCollection}s have the same contents.
	 */
	boolean equalsCollection( UnmodifiableCollection<? extends E> other );

	/**
	 * Finds an element using a predicate.
	 *
	 * @param predicate a predicate to evaluate for each element until it returns {@code true}.
	 *
	 * @return the found element, or {@link Optional#empty()} if no element is found.
	 */
	Optional<E> findFirstElement( Predicate<? super E> predicate );

	/**
	 * Creates a new {@link UnmodifiableCollection} representing each element of this {@link UnmodifiableCollection} converted by a given {@link Function1}.
	 *
	 * @param converter the {@link Function1} invoked to convert each element.
	 *
	 * @return a new {@link UnmodifiableCollection} representing each element of this {@link UnmodifiableCollection} converted by the given {@link Function1}.
	 */
	@Override <T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter );

	<T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter, EqualityComparator<? super T> equalityComparator );

	<T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter );

	<T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter, Function1<Optional<? extends E>,? super T> reverter, EqualityComparator<? super T> equalityComparator );

	<T> UnmodifiableCollection<T> flatMapCollection( Function1<UnmodifiableCollection<T>,E> multiplier );

	/**
	 * Checks whether this {@link UnmodifiableCollection} contains any duplicate elements.
	 *
	 * @return {@code true} if this collection contains any duplicates; {@code false} otherwise.
	 */
	boolean containsDuplicates();

	@Override <T extends E> UnmodifiableCollection<T> upCast();

	@Override <T> UnmodifiableCollection<T> uncheckedCast();

	UnmodifiableCollection<E> chained( UnmodifiableCollection<E> collection );

	UnmodifiableCollection<E> toCollection();

	/**
	 * Checks whether this {@link UnmodifiableCollection} does not contain a certain item.
	 *
	 * @param element the item to check for containment.
	 *
	 * @return {@code true} if the {@link UnmodifiableCollection} does not contain the given item; {@code false} otherwise.
	 *
	 * @see Collection#contains(Object)
	 */
	boolean doesNotContain( E element );

	UnmodifiableList<E> sorted();

	UnmodifiableList<E> sorted( Comparator<? super E> comparator );

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for {@link UnmodifiableCollection}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<E> extends UnmodifiableCollection<E>, UnmodifiableEnumerable.Defaults<E>
	{
		@Override default boolean contains( E element )
		{
			Optional<E> foundElement = tryGet( element );
			return foundElement.isPresent();
		}

		@Override default boolean isEmpty()
		{
			return size() == 0;
		}

		@Override default boolean nonEmpty()
		{
			return size() != 0;
		}

		@Override default boolean containsAny( UnmodifiableEnumerable<? extends E> other )
		{
			for( E element : other )
				if( contains( element ) )
					return true;
			return false;
		}

		@Override default boolean containsAll( UnmodifiableEnumerable<? extends E> other )
		{
			for( E element : other )
				if( !contains( element ) )
					return false;
			return true;
		}

		@Override default E[] toArrayOfObject()
		{
			@SuppressWarnings( { "unchecked", "SuspiciousArrayCast" } )
			E[] array = (E[])new Object[size()];
			int index = 0;
			for( E element : this )
				array[index++] = element;
			return array;
		}

		@Override default E[] toArray( Function1<E[],Integer> arrayFactory )
		{
			E[] array = arrayFactory.invoke( size() );
			int index = 0;
			for( E element : this )
				array[index++] = element;
			return array;
		}

		/**
		 * Checks whether this {@link UnmodifiableCollection} is equal to the given {@link UnmodifiableCollection}.
		 *
		 * PEARL: hibernate collections do not properly implement equals(). From a comment in org.hibernate.collection.internal.PersistentBag.java: "Bag does not respect the
		 * collection API and do an JVM instance comparison to do the equals. The semantic is broken not to have to initialize a collection for a simple equals() operation."
		 * (Jerks!)
		 *
		 * @param other the {@link UnmodifiableCollection} to compare against.
		 *
		 * @return {@code true} if the two {@link UnmodifiableCollection}s have the same contents.
		 */
		@Override default boolean equalsCollection( UnmodifiableCollection<? extends E> other )
		{
			return size() == other.size() && containsAll( other );
		}

		@Override default Optional<E> findFirstElement( Predicate<? super E> predicate )
		{
			for( E element : this )
				if( predicate.test( element ) )
					return Optional.of( element );
			return Optional.empty();
		}

		@Override default boolean containsDuplicates()
		{
			if( size() < 2 )
				return false;
			E[] elements = toArrayOfObject();
			EqualityComparator<? super E> equalityComparator = getEqualityComparator();
			if( elements[0] instanceof Comparable )
			{
				Arrays.sort( elements );
				for( int i = 1; i < elements.length; i++ )
				{
					if( equalityComparator.equals( elements[i - 1], elements[i] ) )
						return true;
				}
			}
			else
			{
				for( int i = 0; i < elements.length; i++ )
				{
					for( int j = 0; j < elements.length; j++ )
					{
						if( j == i )
							continue;
						if( equalityComparator.equals( elements[i], elements[j] ) )
							return true;
					}
				}
			}
			return false;
		}

		/**
		 * Creates a new {@link UnmodifiableCollection} representing each element of this {@link UnmodifiableCollection} converted by a given {@link Function1}.
		 *
		 * @param converter the {@link Function1} invoked to convert each element.
		 *
		 * @return a new {@link UnmodifiableCollection} representing each element of this {@link UnmodifiableCollection} converted by the given {@link Function1}.
		 */
		@Override default <T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter )
		{
			return map( converter, t -> Kit.fail(), DefaultEqualityComparator.getInstance() );
		}

		@Override default <T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter, EqualityComparator<? super T> equalityComparator )
		{
			return map( converter, t -> Kit.fail(), equalityComparator );
		}

		@Override default <T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter,
			Function1<Optional<? extends E>,? super T> reverter )
		{
			EqualityComparator<T> equalityComparator = new PartiallyConvertingEqualityComparator<>( reverter );
			return map( converter, reverter, equalityComparator );
		}

		@Override default <T> UnmodifiableCollection<T> map( Function1<? extends T,? super E> converter,
			Function1<Optional<? extends E>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
		{
			return ConversionCollections.newConvertingCollection( this, converter, reverter, equalityComparator );
		}

		@Override default <T> UnmodifiableCollection<T> flatMapCollection( Function1<UnmodifiableCollection<T>,E> multiplier )
		{
			return new FlatteningCollection<>( this, multiplier );
		}

		@Override default <T extends E> UnmodifiableCollection<T> upCast()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableCollection<T> result = (UnmodifiableCollection<T>)this;
			return result;
		}

		@Override default <T> UnmodifiableCollection<T> uncheckedCast()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableCollection<T> result = (UnmodifiableCollection<T>)this;
			return result;
		}

		@Override default UnmodifiableCollection<E> chained( UnmodifiableCollection<E> collection )
		{
			return ConversionCollections.newChainingCollectionOf( this, collection );
		}

		@Override default UnmodifiableCollection<E> toCollection()
		{
			if( isFrozen() )
				return this;
			return toList();
		}

		@Override default boolean doesNotContain( E element )
		{
			return !contains( element );
		}

		@Override default UnmodifiableList<E> sorted()
		{
			return sorted( DefaultComparator.getInstance() );
		}

		@Override default UnmodifiableList<E> sorted( Comparator<? super E> comparator )
		{
			E[] elements = toArrayOfObject();
			Arrays.sort( elements, comparator );
			return UnmodifiableList.onArray( elements );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link UnmodifiableCollection}.
	 *
	 * @author michael.gr
	 */
	interface Decorator<E> extends Defaults<E>, UnmodifiableEnumerable.Decorator<E>
	{
		UnmodifiableCollection<E> getDecoratedUnmodifiableCollection();

		@Override default UnmodifiableEnumerable<E> getDecoratedUnmodifiableEnumerable()
		{
			return getDecoratedUnmodifiableCollection();
		}

		@Override default EqualityComparator<? super E> getEqualityComparator()
		{
			UnmodifiableCollection<E> decoree = getDecoratedUnmodifiableCollection();
			return decoree.getEqualityComparator();
		}

		@Override default int size()
		{
			UnmodifiableCollection<E> decoree = getDecoratedUnmodifiableCollection();
			return decoree.size();
		}

		@Override default Optional<E> tryGet( E element )
		{
			UnmodifiableCollection<E> decoree = getDecoratedUnmodifiableCollection();
			return decoree.tryGet( element );
		}

		@Override default int getModificationCount()
		{
			UnmodifiableCollection<E> decoree = getDecoratedUnmodifiableCollection();
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
	@ExcludeFromJacocoGeneratedReport @SuppressWarnings( "unused" )
	final class Canary<E> implements Decorator<E>
	{
		@Override public UnmodifiableCollection<E> getDecoratedUnmodifiableCollection()
		{
			return this;
		}
	}
}
