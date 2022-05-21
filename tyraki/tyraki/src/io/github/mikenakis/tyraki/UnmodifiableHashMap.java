package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.Hasher;

/**
 * Unmodifiable Hash Map.
 *
 * @author michael.gr
 */
public interface UnmodifiableHashMap<K, V> extends UnmodifiableMap<K,V>
{
	Hasher<? super K> getKeyHasher();

	interface Defaults<K, V> extends UnmodifiableHashMap<K,V>, UnmodifiableMap.Defaults<K,V>
	{
	}
}
