package io.github.mikenakis.tyraki;

/**
 * A bidirectional (one-to-one) {@link UnmodifiableMap}.
 *
 * @author michael.gr
 */
public interface UnmodifiableBiMap<K, V> extends UnmodifiableMap<K,V>
{
	UnmodifiableMap<V,K> reverse();

	interface Defaults<K, V> extends UnmodifiableBiMap<K,V>, UnmodifiableMap.Defaults<K,V>
	{
	}
}
