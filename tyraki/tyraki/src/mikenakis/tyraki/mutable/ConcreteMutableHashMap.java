package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableCollection;

/**
 * Hash Map.
 *
 * @author michael.gr
 */
class ConcreteMutableHashMap<K, V> extends AbstractMutableHashMap<K,V>
{
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MutableHashEntries entries;

	ConcreteMutableHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections, initialCapacity, fillFactor, keyHasher );
		entries = new MutableHashEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MutableMapKeysCollection<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableMapValuesCollection<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public MutableCollection<Binding<K,V>> mutableEntries()
	{
		return entries;
	}

	@Override public MutableCollection<K> mutableKeys()
	{
		return keys;
	}

	@Override public MutableCollection<V> mutableValues()
	{
		return values;
	}
}
