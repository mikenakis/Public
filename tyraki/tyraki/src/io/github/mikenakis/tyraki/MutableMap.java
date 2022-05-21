package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.exceptions.DuplicateKeyException;
import io.github.mikenakis.tyraki.exceptions.KeyNotFoundException;

import java.util.Optional;

/**
 * Mutable Map.
 *
 * @author michael.gr
 */
public interface MutableMap<K, V> extends UnmodifiableMap<K,V>
{
	/**
	 * Gets all entries.
	 *
	 * @return a {@link MutableCollection} of entries.
	 */
	MutableCollection<Binding<K,V>> mutableEntries();

	/**
	 * Gets all keys.
	 *
	 * @return a {@link MutableCollection} of keys.
	 */
	MutableCollection<K> mutableKeys();

	/**
	 * Gets all values.
	 *
	 * @return a {@link MutableCollection} of values.
	 */
	MutableCollection<V> mutableValues();

	/**
	 * Tries to add a key-value pair; has no effect if the key already exists.
	 *
	 * @param key   the key to add.
	 * @param value the value to add.
	 *
	 * @return {@link Optional#empty()} if the binding was successfully added; the previous value if the key already existed.
	 */
	Optional<V> tryAdd( K key, V value );

	/**
	 * Tries to replace the value associated with a key; has no effect if the key does not already exist.
	 *
	 * @param key   the key of the binding.
	 * @param value the new value for the binding.
	 *
	 * @return {@code true} if the key already existed in the map; {@code false} if the key did not already exist in the map.
	 *
	 * 	NOTE: this method will return {@code true} even if the new value was the same as the old value.
	 */
	boolean tryReplaceValue( K key, V value );

	/**
	 * Tries to remove a key; has no effect if the key does not exist.
	 *
	 * @param key the key to remove.
	 *
	 * @return a {@code true} if the key was found and was removed;  {@code false} if the key was not found.
	 */
	boolean tryRemoveKey( K key );

	/**
	 * Clears the map.
	 *
	 * @return {@code true} if the map contained any items; {@code false} if the map was empty.
	 */
	boolean clear();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link MutableMap}.
	 */
	interface Decorator<K, V> extends Defaults<K,V>, UnmodifiableMap.Decorator<K,V> //TODO rename all 'getDecoree()' method names with 'getDecorated[ClassName]()'
	{
		MutableMap<K,V> getDecoratedMutableMap();

		@Override default UnmodifiableMap<K,V> getDecoratedUnmodifiableMap()
		{
			return getDecoratedMutableMap();
		}

		@Override default UnmodifiableCollection<Binding<K,V>> entries()
		{
			return MutableMap.Defaults.super.entries();
		}

		@Override default UnmodifiableCollection<K> keys()
		{
			return MutableMap.Defaults.super.keys();
		}

		@Override default UnmodifiableCollection<V> values()
		{
			return MutableMap.Defaults.super.values();
		}

		@Override default MutableCollection<Binding<K,V>> mutableEntries()
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.mutableEntries();
		}

		@Override default MutableCollection<K> mutableKeys()
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.mutableKeys();
		}

		@Override default MutableCollection<V> mutableValues()
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.mutableValues();
		}

		@Override default int size()
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.size();
		}

		@Override default boolean tryReplaceValue( K key, V value )
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.tryReplaceValue( key, value );
		}

		@Override default boolean tryRemoveKey( K key )
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.tryRemoveKey( key );
		}

		@Override default Optional<V> tryAdd( K key, V value )
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.tryAdd( key, value );
		}

		@Override default boolean clear()
		{
			MutableMap<K,V> decoree = getDecoratedMutableMap();
			return decoree.clear();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Replaces the value associated with a key; the key must exist.
	 *
	 * @param key   the key whose value is to be replaced.
	 * @param value the value to replace the existing value.
	 */
	void replaceValue( K key, V value );

	/**
	 * Removes a key; the key must exist.
	 *
	 * @param key the key to remove.
	 */
	void removeKey( K key );

	/**
	 * Adds a key-value pair; the key must not already exist.
	 *
	 * @param key   the key to add.
	 * @param value the value to associate to the key.
	 */
	void add( K key, V value );

	void add( K key1, V value1, K key2, V value2 );

	void add( K key1, V value1, K key2, V value2, K key3, V value3 );

	void add( K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4 );

	/**
	 * Adds or replaces a key-value pair.  If the key does not already exist in the map, a new key-value pair is added.  If the key already exists in the map, the associated value
	 * is replaced with the given value.
	 */
	void addOrReplace( K key, V value );

	V computeIfAbsent( K key, Function1<? extends V,? super K> mappingFunction );

	/**
	 * Tries to invoke and remove a key.
	 */
	Optional<V> tryGetAndRemove( K key );

	/**
	 * Gets and removes a key; the key must exist.
	 */
	V getAndRemove( K key );

	void addAll( UnmodifiableMap<K,V> other );

	void addAll( UnmodifiableEnumerable<Binding<K,V>> bindings );

	void addAll( UnmodifiableCollection<Binding<K,V>> bindings );

	<T> void addAll( UnmodifiableCollection<T> items, Function1<K,T> keyFromItemConverter, Function1<V,T> valueFromItemConverter );

	void addAll( UnmodifiableCollection<K> keys, Function1<V,K> valueFromKeyConverter );

	/**
	 * Default methods for {@link MutableMap}.
	 */
	interface Defaults<K, V> extends MutableMap<K,V>, UnmodifiableMap.Defaults<K,V>
	{
		@Override default UnmodifiableCollection<Binding<K,V>> entries()
		{
			return mutableEntries();
		}

		@Override default UnmodifiableCollection<K> keys()
		{
			return mutableKeys();
		}

		@Override default UnmodifiableCollection<V> values()
		{
			return mutableValues();
		}

		@Override default void replaceValue( K key, V value )
		{
			assert key != null;
			if( !tryReplaceValue( key, value ) )
				throw new KeyNotFoundException( key );
		}

		@Override default void removeKey( K key )
		{
			assert key != null;
			if( !tryRemoveKey( key ) )
				throw new KeyNotFoundException( key );
		}

		@Override default void add( K key, V value )
		{
			assert key != null;
			Optional<V> previous = tryAdd( key, value );
			assert previous.isEmpty() : new DuplicateKeyException( key, value, previous.get() );
		}

		@Override default void add( K key1, V value1, K key2, V value2 )
		{
			add( key1, value1 );
			add( key2, value2 );
		}

		@Override default void add( K key1, V value1, K key2, V value2, K key3, V value3 )
		{
			add( key1, value1 );
			add( key2, value2 );
			add( key3, value3 );
		}

		@Override default void add( K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4 )
		{
			add( key1, value1 );
			add( key2, value2 );
			add( key3, value3 );
			add( key4, value4 );
		}

		@Override default void addOrReplace( K key, V value )
		{
			assert key != null;
			if( tryAdd( key, value ).isEmpty() )
				return;
			replaceValue( key, value );
		}

		@Override default V computeIfAbsent( K key, Function1<? extends V,? super K> mappingFunction )
		{
			assert key != null;
			assert mappingFunction != null;
			Optional<V> value = tryGet( key );
			if( value.isPresent() )
				return value.get();
			V newValue = mappingFunction.invoke( key );
			add( key, newValue );
			return newValue;
		}

		@Override default Optional<V> tryGetAndRemove( K key )
		{
			assert key != null;
			Optional<V> value = tryGet( key );
			if( value.isEmpty() )
				return Optional.empty();
			removeKey( key );
			return value;
		}

		@Override default V getAndRemove( K key )
		{
			assert key != null;
			V value = get( key );
			removeKey( key );
			return value;
		}

		@Override default void addAll( UnmodifiableMap<K,V> other )
		{
			mutableEntries().addAll( other.entries() );
		}

		@Override default void addAll( UnmodifiableEnumerable<Binding<K,V>> bindings )
		{
			mutableEntries().addAll( bindings );
		}

		@Override default void addAll( UnmodifiableCollection<Binding<K,V>> bindings )
		{
			mutableEntries().addAll( bindings );
		}

		@Override default <T> void addAll( UnmodifiableCollection<T> items, Function1<K,T> keyFromItemConverter, Function1<V,T> valueFromItemConverter )
		{
			for( T item : items )
			{
				K key = keyFromItemConverter.invoke( item );
				V value = valueFromItemConverter.invoke( item );
				add( key, value );
			}
		}

		@Override default void addAll( UnmodifiableCollection<K> keys, Function1<V,K> valueFromKeyConverter )
		{
			for( K key : keys )
			{
				V value = valueFromKeyConverter.invoke( key );
				add( key, value );
			}
		}
	}
}
