package mikenakis.tyraki;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.ObjectHasher;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.mutation.TemporaryMutationContext;
import mikenakis.tyraki.conversion.ConversionCollections;
import mikenakis.tyraki.immutable.ImmutableCollections;
import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Unmodifiable Hash Map.
 *
 * @author michael.gr
 */
public interface UnmodifiableArrayHashMap<K, V> extends UnmodifiableHashMap<K,V>, UnmodifiableArrayMap<K,V>
{
	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> fromKeys( UnmodifiableCollection<K> keys, Function1<V,K> valueFromKeyConverter )
	{
		if( keys.isEmpty() )
			return of();
		UnmodifiableMap<K,V> temporaryMap = ConversionCollections.newMapOnKeyCollection( keys, valueFromKeyConverter );
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = keys.getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( temporaryMap.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> fromValues( UnmodifiableCollection<V> values, Function1<K,V> keyFromValueConverter )
	{
		if( values.isEmpty() )
			return of();
		UnmodifiableMap<K,V> temporaryMap = ConversionCollections.newMapOnValueCollection( values, keyFromValueConverter );
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = values.getEqualityComparator();
		return from( temporaryMap.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Gets the empty {@link UnmodifiableArrayHashMap}.
	 *
	 * @param <K> the type of the keys of the {@link UnmodifiableArrayHashMap}.
	 * @param <V> the type of the values of the {@link UnmodifiableArrayHashMap}.
	 *
	 * @return the empty immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of() { return ImmutableCollections.emptyArrayHashMap(); }

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of( K k0, V v0 ) { return ofBindings( MapEntry.of( k0, v0 ) ); }

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of( K k0, V v0, K k1, V v1 ) { return ofBindings( MapEntry.of( k0, v0 ), MapEntry.of( k1, v1 ) ); }

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of( K k0, V v0, K k1, V v1, K k2, V v2 ) { return ofBindings( MapEntry.of( k0, v0 ), MapEntry.of( k1, v1 ), MapEntry.of( k2, v2 ) ); }

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of( K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3 ) { return ofBindings( MapEntry.of( k0, v0 ), MapEntry.of( k1, v1 ), MapEntry.of( k2, v2 ), MapEntry.of( k3, v3 ) ); }

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> of( K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4 ) { return ofBindings( MapEntry.of( k0, v0 ), MapEntry.of( k1, v1 ), MapEntry.of( k2, v2 ), MapEntry.of( k3, v3 ), MapEntry.of( k4, v4 ) ); }

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <K, V> UnmodifiableArrayHashMap<K,V> ofBindings( Binding<K,V>... arrayOfBindings )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = UnmodifiableList.onArray( arrayOfBindings );
		return from( bindings );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 *
	 * @param bindings                the {@link Binding}s.
	 * @param fillFactor              the fill factor to use.
	 * @param keyHasher               the {@link Hasher} to use for hashing keys.
	 * @param keyEqualityComparator   the {@link EqualityComparator} to use for comparing keys.
	 * @param valueEqualityComparator the {@link EqualityComparator} to use for comparing values.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableCollection<Binding<K,V>> bindings, float fillFactor, Hasher<? super K> keyHasher, //
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		if( bindings.isEmpty() )
			return ImmutableCollections.emptyArrayHashMap();
		return Kit.tryGetWith( TemporaryMutationContext.of(), mutationContext -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( mutationContext );
			FreezableArrayHashMap<K,V> mutableMap = mutableCollections.newArrayHashMap( bindings.size(), fillFactor, keyHasher, //
				keyEqualityComparator, valueEqualityComparator );
			mutableMap.addAll( bindings );
			return mutableMap.frozen();
		} );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 *
	 * @param bindingsEnumerable the {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableEnumerable<Binding<K,V>> bindingsEnumerable )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = bindingsEnumerable.toList();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, ObjectHasher.INSTANCE, DefaultEqualityComparator.getInstance(), //
			DefaultEqualityComparator.getInstance() );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 *
	 * @param bindingsEnumerable      the {@link Binding}s.
	 * @param fillFactor              the fill factor to use.
	 * @param keyHasher               the {@link Hasher} to use for hashing keys.
	 * @param keyEqualityComparator   the {@link EqualityComparator} to use for comparing keys.
	 * @param valueEqualityComparator the {@link EqualityComparator} to use for comparing values.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableEnumerable<Binding<K,V>> bindingsEnumerable, float fillFactor, Hasher<? super K> keyHasher, //
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = bindingsEnumerable.toList();
		return from( bindings, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashMap}.
	 *
	 * @param keyHasher               the {@link Hasher<K>} to use for hashing keys.
	 * @param keyEqualityComparator   the {@link EqualityComparator<K>} to use for comparing keys.
	 * @param valueEqualityComparator the {@link EqualityComparator<V>} to use for comparing values.
	 * @param bindings                the {@link Binding}s that the {@link UnmodifiableMap} is to contain.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( Hasher<K> keyHasher, EqualityComparator<K> keyEqualityComparator, //
		EqualityComparator<V> valueEqualityComparator, UnmodifiableCollection<Binding<K,V>> bindings )
	{
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashMap}.
	 *
	 * @param bindings the {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableCollection<Binding<K,V>> bindings )
	{
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashMap}.
	 *
	 * @param bindings the {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableList<Binding<K,V>> bindings )
	{
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayMap}.
	 *
	 * @param map the {@link UnmodifiableMap} providing the {@link Binding}s.
	 *
	 * @return a new {@link UnmodifiableArrayMap}.
	 */
	static <K, V> UnmodifiableArrayMap<K,V> from( UnmodifiableMap<K,V> map )
	{
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = map.keys().getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = map.values().getEqualityComparator();
		UnmodifiableCollection<Binding<K,V>> entries = map.entries();
		return from( entries, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new {@link UnmodifiableArrayHashMap}.
	 *
	 * @param map the {@link UnmodifiableHashMap} providing the {@link Binding}s that the {@link UnmodifiableMap} is to contain.
	 *
	 * @return a new {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableHashMap<K,V> map )
	{
		Hasher<? super K> keyHasher = map.getKeyHasher();
		EqualityComparator<? super K> keyEqualityComparator = map.keys().getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = map.values().getEqualityComparator();
		return from( map.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( UnmodifiableList<K> keys, Function1<V,K> valueFromKeyConverter )
	{
		if( keys.isEmpty() )
			return of();
		UnmodifiableList<Binding<K,V>> entries = keys.map( key -> MapEntry.of( key, valueFromKeyConverter.invoke( key ) ) );
		//UnmodifiableArrayMap<K,V> temporaryMap = ConversionCollections.newArrayMapOnKeyList( keys, valueFromKeyConverter );
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = keys.getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( entries/*temporaryMap.getEntries()*/, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableArrayHashMap}.
	 */
	static <K, V> UnmodifiableArrayHashMap<K,V> from( Function1<K,V> keyFromValueConverter, UnmodifiableList<V> values )
	{
		if( values.isEmpty() )
			return of();
		UnmodifiableList<Binding<K,V>> entries = values.map( value -> MapEntry.of( keyFromValueConverter.invoke( value ), value ) );
		//UnmodifiableArrayMap<K,V> temporaryMap = ConversionCollections.newArrayMapOnValueList( values, keyFromValueConverter );
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = values.getEqualityComparator();
		return from( entries/*temporaryMap.getEntries()*/, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<KK, VV> UnmodifiableArrayHashMap<KK,VV> castArrayHashMap();

	interface Defaults<K, V> extends UnmodifiableArrayHashMap<K,V>, UnmodifiableHashMap.Defaults<K,V>, UnmodifiableArrayMap.Defaults<K,V>
	{
		@Override default <KK, VV> UnmodifiableArrayHashMap<KK,VV> castArrayHashMap()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableArrayHashMap<KK,VV> result = (UnmodifiableArrayHashMap<KK,VV>)this;
			return result;
		}
	}
}
