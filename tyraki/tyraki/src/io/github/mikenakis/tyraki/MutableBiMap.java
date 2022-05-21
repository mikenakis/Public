package io.github.mikenakis.tyraki;

/**
 * A bidirectional (one-to-one) {@link MutableMap}.
 *
 * @author michael.gr
 */
public interface MutableBiMap<K, V> extends MutableMap<K,V>, UnmodifiableBiMap<K,V>
{
	@Override MutableMap<V,K> reverse();

	interface Defaults<K, V> extends MutableBiMap<K,V>, MutableMap.Defaults<K,V>, UnmodifiableBiMap.Defaults<K,V>
	{
	}
}
