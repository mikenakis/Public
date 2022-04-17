package mikenakis.tyraki;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.ObjectHasher;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.coherence.FreezableCoherence;
import mikenakis.tyraki.conversion.ConversionCollections;
import mikenakis.tyraki.immutable.ImmutableCollections;
import mikenakis.tyraki.mutable.MutableCollections;

/**
 * Unmodifiable Hash Map.
 *
 * @author michael.gr
 */
public interface UnmodifiableHashMap<K, V> extends UnmodifiableMap<K,V>
{
	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 *
	 * @param bindings                the {@link Binding}s.
	 * @param keyHasher               the {@link Hasher} to use for hashing keys.
	 * @param keyEqualityComparator   the {@link EqualityComparator} to use for comparing keys.
	 * @param valueEqualityComparator the {@link EqualityComparator} to use for comparing values.
	 *
	 * @return a new {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableCollection<Binding<K,V>> bindings, float fillFactor, Hasher<? super K> keyHasher, //
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		if( bindings.isEmpty() )
			return of();
		return Kit.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashMap<K,V> mutableMap = mutableCollections.newLinkedHashMap( bindings.size(), fillFactor, keyHasher, //
				keyEqualityComparator, valueEqualityComparator );
			mutableMap.addAll( bindings );
			return mutableMap;
		} );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 *
	 * @param bindings                the {@link Binding}s.
	 * @param keyHasher               the {@link Hasher} to use for hashing keys.
	 * @param keyEqualityComparator   the {@link EqualityComparator} to use for comparing keys.
	 * @param valueEqualityComparator the {@link EqualityComparator} to use for comparing values.
	 *
	 * @return a new {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableEnumerable<Binding<K,V>> bindings, float fillFactor, Hasher<? super K> keyHasher, //
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		if( bindings.isEmpty() )
			return of();
		return Kit.tryGetWith( FreezableCoherence.of(), coherence -> //
		{
			MutableCollections mutableCollections = MutableCollections.of( coherence );
			MutableHashMap<K,V> mutableMap = mutableCollections.newLinkedHashMap( 1, fillFactor, keyHasher, //
				keyEqualityComparator, valueEqualityComparator );
			mutableMap.addAll( bindings );
			return mutableMap;
		} );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 *
	 * @param bindings the {@link Binding}s that the {@link UnmodifiableMap} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableList<Binding<K,V>> bindings )
	{
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 *
	 * @param bindings the {@link Binding}s that the {@link UnmodifiableMap} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableEnumerable<Binding<K,V>> bindings )
	{
		return from( bindings.toList() );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 *
	 * @param bindings the {@link Binding}s that the {@link UnmodifiableMap} is to contain.
	 *
	 * @return a new {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableCollection<Binding<K,V>> bindings )
	{
		if( bindings.isEmpty() )
			return of();
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableMap<K,V> map, Hasher<? super K> keyHasher )
	{
		if( map.isEmpty() )
			return of();
		EqualityComparator<? super K> keyEqualityComparator = map.keys().getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = map.values().getEqualityComparator();
		return from( map.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableMap<K,V> map )
	{
		if( map.isEmpty() )
			return of();
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = map.keys().getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = map.values().getEqualityComparator();
		return from( map.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( UnmodifiableHashMap<K,V> hashMap )
	{
		if( hashMap.isEmpty() )
			return of();
		Hasher<? super K> keyHasher = hashMap.getKeyHasher();
		EqualityComparator<? super K> keyEqualityComparator = hashMap.keys().getEqualityComparator();
		EqualityComparator<? super V> valueEqualityComparator = hashMap.values().getEqualityComparator();
		return from( hashMap.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> from( Function1<K,V> keyFromValueConverter, UnmodifiableCollection<V> values )
	{
		if( values.isEmpty() )
			return of();
		UnmodifiableCollection<Binding<K,V>> entries = values.map( value -> MapEntry.of( keyFromValueConverter.invoke( value ), value ) );
		return from( entries );
	}

	static <K, V> UnmodifiableHashMap<K,V> of( UnmodifiableHashMap<K,V> map )
	{
		return from( map );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> fromKeys( UnmodifiableEnumerable<K> keys, Function1<V,K> valueFromKeyConverter )
	{
		if( keys.isEmpty() )
			return of();
		UnmodifiableEnumerable<Binding<K,V>> bindings = keys.map( key -> MapEntry.of( key, valueFromKeyConverter.invoke( key ) ) );
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		EqualityComparator<? super V> valueEqualityComparator = DefaultEqualityComparator.getInstance();
		return from( bindings, ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> fromValues( UnmodifiableCollection<V> values, Function1<K,V> keyFromValueConverter )
	{
		Hasher<? super K> keyHasher = ObjectHasher.INSTANCE;
		EqualityComparator<? super K> keyEqualityComparator = DefaultEqualityComparator.getInstance();
		return fromValues( values, keyFromValueConverter, keyHasher, keyEqualityComparator );
	}

	/**
	 * Creates a new immutable {@link UnmodifiableHashMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> fromValues( UnmodifiableCollection<V> values, Function1<K,V> keyFromValueConverter, Hasher<? super K> keyHasher, //
		EqualityComparator<? super K> keyEqualityComparator )
	{
		if( values.isEmpty() )
			return of();
		UnmodifiableMap<K,V> temporaryMap = ConversionCollections.newMapOnValueCollection( values, keyFromValueConverter );
		EqualityComparator<? super V> valueEqualityComparator = values.getEqualityComparator();
		return from( temporaryMap.entries(), ImmutableCollections.DEFAULT_FILL_FACTOR, keyHasher, keyEqualityComparator, valueEqualityComparator );
	}

	/**
	 * Gets the empty {@link UnmodifiableHashMap}.
	 *
	 * @param <K> the type of the keys of the {@link UnmodifiableMap}.
	 * @param <V> the type of the values of the {@link UnmodifiableMap}.
	 *
	 * @return the empty Unmodifiable {@link UnmodifiableMap}.
	 */
	static <K, V> UnmodifiableHashMap<K,V> of()
	{
		return UnmodifiableArrayHashMap.of();
	}

	@SafeVarargs @SuppressWarnings( "varargs" ) //for -Xlint
	static <K, V> UnmodifiableHashMap<K,V> of( Binding<K,V>... arrayOfBindings )
	{
		UnmodifiableCollection<Binding<K,V>> bindings = UnmodifiableList.onArray( arrayOfBindings );
		return from( bindings );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Hasher<? super K> getKeyHasher();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	<KK, VV> UnmodifiableHashMap<KK,VV> castHashMap();

	interface Defaults<K, V> extends UnmodifiableHashMap<K,V>, UnmodifiableMap.Defaults<K,V>
	{
		@Override default <KK, VV> UnmodifiableHashMap<KK,VV> castHashMap()
		{
			@SuppressWarnings( "unchecked" ) UnmodifiableHashMap<KK,VV> result = (UnmodifiableHashMap<KK,VV>)this;
			return result;
		}
	}
}
