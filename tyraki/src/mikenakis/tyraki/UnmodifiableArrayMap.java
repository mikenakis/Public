package mikenakis.tyraki;

import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.conversion.ConversionCollections;
import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Unmodifiable Array Map.
 *
 * @author michael.gr
 */
public interface UnmodifiableArrayMap<K, V> extends UnmodifiableMap<K,V>
{
	/**
	 * Creates a new immutable {@link UnmodifiableArrayMap}.
	 *
	 * @param bindings the {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableCollection<Binding<K,V>> bindings )
	{
		if( bindings.isEmpty() )
			return of();
		return MutableCollections.tryGetWithLocal( mutableCollections -> //
		{
			FreezableArrayMap<K,V> mutableMap = mutableCollections.newArrayMap();
			mutableMap.addAll( bindings );
			return mutableMap.frozen();
		} );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayMap} from an {@link UnmodifiableEnumerable} of {@link Binding}s.
	 *
	 * @param bindingsEnumerable an {@link UnmodifiableEnumerable} with {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableEnumerable<Binding<K,V>> bindingsEnumerable )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = bindingsEnumerable.toList();
		return from( bindings );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableList<K> keys, Function1<V,K> valueFromKeyConverter )
	{
		if( keys.isEmpty() )
			return of();
		UnmodifiableList<Binding<K,V>> entries = keys.map( key -> MapEntry.of( key, valueFromKeyConverter.invoke( key ) ) );
		return from( entries );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( Function1<K,V> keyFromValueConverter, UnmodifiableCollection<V> values )
	{
		if( values.isEmpty() )
			return of();
		UnmodifiableCollection<Binding<K,V>> entries = values.map( value -> MapEntry.of( keyFromValueConverter.invoke( value ), value ) );
		return from( entries );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableMap<K,V> map )
	{
		if( map.isEmpty() )
			return of();
		return from( map.entries() );
	}

	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableArrayMap<K,V> map )
	{
		if( map.isFrozen() )
			return map;
		return from( map );
	}

	/**
	 * Gets the empty {@link UnmodifiableArrayMap}.
	 *
	 * @param <K> the type of the keys of the {@link UnmodifiableArrayMap}.
	 * @param <V> the type of the values of the {@link UnmodifiableArrayMap}.
	 *
	 * @return the empty immutable {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> of()
	{
		return UnmodifiableArrayHashMap.of();
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <K, V> UnmodifiableArrayMap<K,V> of( Binding<K,V>... arrayOfBindings )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = UnmodifiableList.onArray( arrayOfBindings );
		return from( bindings );
	}

	static <K, V> UnmodifiableArrayMap<K,V> of( UnmodifiableCollection<Binding<K,V>> bindings )
	{
		return from( bindings );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets an {@link UnmodifiableList} of all entries.
	 *
	 * @return an {@link UnmodifiableList} of all entries.
	 */
	@Override UnmodifiableList<Binding<K,V>> entries();

	/**
	 * Gets an {@link UnmodifiableArraySet} of all keys.
	 *
	 * @return an {@link UnmodifiableArraySet} of all keys.
	 */
	@Override UnmodifiableArraySet<K> keys();

	/**
	 * Gets an {@link UnmodifiableList} of all values.
	 *
	 * @return an {@link UnmodifiableList} of all values.
	 */
	@Override UnmodifiableList<V> values();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets an {@link UnmodifiableArrayMap} in which each key is cast to a given type.
	 */
	<TK> UnmodifiableArrayMap<TK,V> castArrayKeys();

	/**
	 * Creates a new {@link UnmodifiableArrayMap} representing each {@link Binding} of this {@link UnmodifiableMap} where the value is cast down to the given type.
	 *
	 * @param <TV> the value type to cast to.
	 *
	 * @return a new {@link UnmodifiableArrayMap} representing all {@link Binding}s of this {@link UnmodifiableMap} where the value is cast from FV to TV.
	 */
	<TV> UnmodifiableArrayMap<K,TV> castArrayValues();

	/**
	 * Gets an {@link UnmodifiableArrayMap} in which each key and each value are cast to given types.
	 */
	<TK, TV> UnmodifiableArrayMap<TK,TV> castArrayMap();

	/**
	 * Gets an {@link UnmodifiableArrayMap} which is the reverse of this map.  Fails if this map is not one-to-one.
	 */
	UnmodifiableArrayMap<V,K> transposedArrayMap();

	/**
	 * Gets an {@link UnmodifiableArrayMap} which contains all elements of this {@link UnmodifiableArrayMap} followed by all elements of a given {@link UnmodifiableArrayMap}.
	 */
	UnmodifiableArrayMap<K,V> chained( UnmodifiableArrayMap<K,V> other );

	interface Defaults<K, V> extends UnmodifiableArrayMap<K,V>, UnmodifiableMap.Defaults<K,V>
	{
		@Override default <TK> UnmodifiableArrayMap<TK,V> castArrayKeys()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableArrayMap<TK,V> result = (UnmodifiableArrayMap<TK,V>)this;
			return result;
		}

		@Override default <TV> UnmodifiableArrayMap<K,TV> castArrayValues()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableArrayMap<K,TV> result = (UnmodifiableArrayMap<K,TV>)this;
			return result;
		}

		@Override default <TK, TV> UnmodifiableArrayMap<TK,TV> castArrayMap()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableArrayMap<TK,TV> result = (UnmodifiableArrayMap<TK,TV>)this;
			return result;
		}

		@Override default UnmodifiableArrayMap<V,K> transposedArrayMap()
		{
			return UnmodifiableArrayMap.from( k -> get( k ), keys() );
		}

		@Override default UnmodifiableArrayMap<K,V> chained( UnmodifiableArrayMap<K,V> other )
		{
			return ConversionCollections.newChainingArrayMapOf( this, other );
		}
	}
}
