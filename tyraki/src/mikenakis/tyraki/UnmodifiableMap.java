package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.mutation.FreezableCoherence;
import mikenakis.tyraki.conversion.ConversionCollections;
import mikenakis.tyraki.exceptions.KeyNotFoundException;
import mikenakis.tyraki.immutable.ImmutableCollections;
import mikenakis.tyraki.mutable.MutableCollections;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Unmodifiable Map.
 *
 * @author michael.gr
 */
public interface UnmodifiableMap<K, V>
{
	/**
	 * Gets the empty {@link UnmodifiableMap}.
	 *
	 * @param <K> the type of the keys of the {@link UnmodifiableMap}.
	 * @param <V> the type of the values of the {@link UnmodifiableMap}.
	 *
	 * @return the empty Unmodifiable {@link UnmodifiableMap}.
	 */
	static <K, V> UnmodifiableMap<K,V> of()
	{
		return ImmutableCollections.emptyArrayHashMap();
	}

	static <K, V> UnmodifiableMap<K,V> of( UnmodifiableMap<K,V> map )
	{
		return UnmodifiableHashMap.from( map );
	}

	/**
	 * Casts keys to a supertype.
	 */
	static <K extends TK, V, TK> UnmodifiableMap<TK,V> downCastKeys( UnmodifiableMap<K,V> map )
	{
		@SuppressWarnings( "unchecked" ) UnmodifiableMap<TK,V> result = (UnmodifiableMap<TK,V>)map;
		return result;
	}

	/**
	 * Casts values to a supertype.
	 */
	static <K, V extends TV, TV> UnmodifiableMap<K,TV> downCastValues( UnmodifiableMap<K,V> map )
	{
		@SuppressWarnings( "unchecked" ) UnmodifiableMap<K,TV> result = (UnmodifiableMap<K,TV>)map;
		return result;
	}

	static <T, K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( UnmodifiableCollection<T> items, Function1<K,T> keyFromItemConverter, Function1<V,T> valueFromItemConverter )
	{
		return Kit.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashMap<K,V> freezableMap = mutableCollections.newLinkedHashMap();
			for( T item : items )
			{
				K key = keyFromItemConverter.invoke( item );
				V value = valueFromItemConverter.invoke( item );
				freezableMap.add( key, value );
			}
			return freezableMap;
		} );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( UnmodifiableCollection<K> keys, Function1<V,K> valueFromKeyConverter )
	{
		return Kit.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashMap<K,V> freezableMap = mutableCollections.newLinkedHashMap();
			for( K key : keys )
			{
				V value = valueFromKeyConverter.invoke( key );
				freezableMap.add( key, value );
			}
			return freezableMap;
		} );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( K key1, V value1 )
	{
		return newLinkedHashMap( MapEntry.of( key1, value1 ) );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( K key1, V value1, K key2, V value2 )
	{
		return UnmodifiableMap.<K,V>newLinkedHashMap( MapEntry.of( key1, value1 ), MapEntry.of( key2, value2 ) );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( K key1, V value1, K key2, V value2, K key3, V value3 )
	{
		return newLinkedHashMap( MapEntry.of( key1, value1 ), MapEntry.of( key2, value2 ), MapEntry.of( key3, value3 ) );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4 )
	{
		return UnmodifiableMap.<K,V>newLinkedHashMap( MapEntry.of( key1, value1 ), MapEntry.of( key2, value2 ), MapEntry.of( key3, value3 ), MapEntry.of( key4, value4 ) );
	}

	@SuppressWarnings( "varargs" ) @SafeVarargs static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( Binding<K,V>... bindings )
	{
		return newLinkedHashMap( UnmodifiableList.of( bindings ) );
	}

	static <K, V> UnmodifiableHashMap<K,V> newLinkedHashMap( UnmodifiableEnumerable<Binding<K,V>> bindings )
	{
		return Kit.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashMap<K,V> freezableMap = mutableCollections.newLinkedHashMap();
			for( var binding : bindings )
				freezableMap.add( binding.getKey(), binding.getValue() );
			return freezableMap;
		} );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	boolean isImmutableAssertion();

	/**
	 * Gets the number of entries.
	 *
	 * @return the number of entries.
	 */
	int size();

	/**
	 * Tries to get the binding of a given key.
	 *
	 * @param key the key to look up
	 *
	 * @return the {@link Binding} of the given key, or {@link Optional#empty()} if the key was not found.
	 */
	Optional<Binding<K,V>> tryGetBindingByKey( K key );

	/**
	 * Gets all entries.
	 *
	 * @return an {@link UnmodifiableCollection} of all entries.
	 */
	UnmodifiableCollection<Binding<K,V>> entries();

	/**
	 * Gets all keys.
	 *
	 * @return an {@link UnmodifiableCollection} of all keys.
	 */
	UnmodifiableCollection<K> keys();

	/**
	 * Gets all values.
	 *
	 * @return an {@link UnmodifiableCollection} of all values.
	 */
	UnmodifiableCollection<V> values();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Checks whether {@code this} is empty.
	 */
	boolean isEmpty();

	/**
	 * Checks whether {@code this} is non-empty.
	 */
	boolean nonEmpty();

	/**
	 * Tries to get the bindings of a given value.
	 *
	 * @param value the value to search for.
	 *
	 * @return an {@link UnmodifiableEnumerable} of all the {@link Binding}s of the given value.
	 */
	UnmodifiableEnumerable<Binding<K,V>> tryGetBindingsByValue( V value );

	/**
	 * Tries to get the value bound to a key.
	 *
	 * @param key the key to look up
	 *
	 * @return the value bound to the given key, or {@link Optional#empty()} if the key was not found.
	 */
	Optional<V> tryGet( K key );

	/**
	 * Checks whether a given key exists.
	 *
	 * @param key the key to check.
	 *
	 * @return {@code true} if the key exists, {@code false} otherwise.
	 */
	boolean containsKey( K key );

	/**
	 * Gets the value bound to a key.
	 *
	 * @param key the key to look up
	 *
	 * @return the value bound to the given key.
	 */
	V get( K key );

	/**
	 * Checks whether this {@link UnmodifiableMap} contains a value.
	 *
	 * @param value the value to search for.
	 *
	 * @return {@code true} if the {@link UnmodifiableMap} contains the given value.
	 */
	boolean containsValue( V value );

	/**
	 * Gets an {@link UnmodifiableMap} which is the reverse of this map.  Fails if this map is not one-to-one.
	 */
	UnmodifiableMap<V,K> transposed();

	/**
	 * Creates a new {@link UnmodifiableMap} with keys converted using given converter.
	 */
	<T> UnmodifiableMap<T,V> convertedKeys( Function1<T,K> converter );

	/**
	 * Creates a new {@link UnmodifiableMap} with values converted using given converter.
	 */
	<T> UnmodifiableMap<K,T> convertedValues( Function1<T,V> converter );

	boolean equalsMap( UnmodifiableMap<K,V> other );

	UnmodifiableMap<K,V> chain( UnmodifiableMap<K,V> other );

	/**
	 * Creates a new {@link UnmodifiableMap} which represents this map filtered using a given predicate.
	 *
	 * @param predicate the {@link Predicate} to apply on each binding to determine whether it should pass the filter.
	 *
	 * @return a new {@link UnmodifiableMap}.
	 */
	UnmodifiableMap<K,V> filter( Predicate<Binding<K,V>> predicate );

	UnmodifiableMap<V,K> transposedMap();

	@SuppressWarnings( "MethodOverloadsMethodOfSuperclass" ) boolean equals( UnmodifiableMap<?,?> other );

	/**
	 * Default methods for {@link UnmodifiableMap}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<K, V> extends UnmodifiableMap<K,V>
	{
		@Override default boolean isEmpty()
		{
			return size() == 0;
		}

		@Override default boolean nonEmpty()
		{
			return size() != 0;
		}

		@Override default UnmodifiableEnumerable<Binding<K,V>> tryGetBindingsByValue( V value )
		{
			EqualityComparator<? super V> valueEqualityComparator = values().getEqualityComparator();
			return entries().filter( binding -> valueEqualityComparator.equals( value, binding.getValue() ) );
		}

		@Override default V get( K key )
		{
			assert key != null;
			Optional<Binding<K,V>> binding = tryGetBindingByKey( key );
			assert binding.isPresent() : new KeyNotFoundException( key );
			return binding.get().getValue();
		}

		@Override default Optional<V> tryGet( K key )
		{
			assert key != null;
			Optional<Binding<K,V>> binding = tryGetBindingByKey( key );
			if( binding.isEmpty() )
				return Optional.empty();
			return Optional.ofNullable( binding.get().getValue() );
		}

		@Override default boolean containsKey( K key )
		{
			assert key != null;
			Optional<Binding<K,V>> binding = tryGetBindingByKey( key );
			return binding.isPresent();
		}

		@Override default boolean containsValue( V value )
		{
			EqualityComparator<? super V> valueEqualityComparator = values().getEqualityComparator();
			for( V existingValue : values() )
				if( valueEqualityComparator.equals( value, existingValue ) )
					return true;
			return false;
		}

		@Override default UnmodifiableMap<V,K> transposed()
		{
			return UnmodifiableHashMap.fromValues( keys(), this::get );
		}

		@Override default <T> UnmodifiableMap<T,V> convertedKeys( Function1<T,K> converter )
		{
			return ConversionCollections.newKeyConvertingAndFilteringMap( this, converter );
		}

		@Override default <T> UnmodifiableMap<K,T> convertedValues( Function1<T,V> converter )
		{
			return ConversionCollections.newValueConvertingAndFilteringMap( this, converter );
		}

		@Override default boolean equalsMap( UnmodifiableMap<K,V> other )
		{
			if( size() != other.size() )
				return false;
			EqualityComparator<? super V> equalityComparator = values().getEqualityComparator();
			for( Binding<K,V> binding : entries() )
			{
				Optional<V> otherValue = other.tryGet( binding.getKey() );
				if( otherValue.isEmpty() )
					return false;
				if( !equalityComparator.equals( binding.getValue(), otherValue.get() ) )
					return false;
			}
			return true;
		}

		@Override default UnmodifiableMap<K,V> chain( UnmodifiableMap<K,V> other )
		{
			return ConversionCollections.newChainingMapOf( this, other );
		}

		@Override default UnmodifiableMap<K,V> filter( Predicate<Binding<K,V>> predicate )
		{
			return ConversionCollections.newFilteringMap( this, predicate );
		}

		@Override default UnmodifiableMap<V,K> transposedMap()
		{
			return UnmodifiableHashMap.from( this::get, keys() );
		}

		@Override default boolean equals( UnmodifiableMap<?,?> other )
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableMap<K,V> kin = (UnmodifiableMap<K,V>)other;
			return equalsMap( kin );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link UnmodifiableMap}.
	 *
	 * @author michael.gr
	 */
	interface Decorator<K, V> extends Defaults<K,V>
	{
		UnmodifiableMap<K,V> getDecoratedUnmodifiableMap();

		@Override default boolean isImmutableAssertion()
		{
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.isImmutableAssertion();
		}

		@Override default int size()
		{
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.size();
		}

		@Override default Optional<Binding<K,V>> tryGetBindingByKey( K key )
		{
			assert key != null;
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.tryGetBindingByKey( key );
		}

		@Override default UnmodifiableCollection<Binding<K,V>> entries()
		{
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.entries();
		}

		@Override default UnmodifiableCollection<K> keys()
		{
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.keys();
		}

		@Override default UnmodifiableCollection<V> values()
		{
			UnmodifiableMap<K,V> decoree = getDecoratedUnmodifiableMap();
			return decoree.values();
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
	final class Canary<K, V> implements Decorator<K,V>
	{
		@Override public UnmodifiableMap<K,V> getDecoratedUnmodifiableMap()
		{
			return this;
		}
	}
}
