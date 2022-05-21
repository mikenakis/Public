package io.github.mikenakis.tyraki;

/**
 * Mutable Array Hash Map.
 *
 * @author michael.gr
 */
public interface MutableArrayHashMap<K, V> extends MutableHashMap<K,V>, MutableArrayMap<K,V>, UnmodifiableArrayHashMap<K,V>
{
	interface Defaults<K, V> extends MutableArrayHashMap<K,V>, MutableHashMap.Defaults<K,V>, MutableArrayMap.Defaults<K,V>, UnmodifiableArrayHashMap.Defaults<K,V>
	{
		@Override default UnmodifiableList<Binding<K,V>> entries()
		{
			return MutableArrayMap.Defaults.super.entries();
		}

		@Override default UnmodifiableArraySet<K> keys()
		{
			return MutableArrayMap.Defaults.super.keys();
		}

		@Override default UnmodifiableList<V> values()
		{
			return MutableArrayMap.Defaults.super.values();
		}
	}
}
