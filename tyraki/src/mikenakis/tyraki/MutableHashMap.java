package mikenakis.tyraki;

/**
 * Mutable Hash Map.
 *
 * @author michael.gr
 */
public interface MutableHashMap<K, V> extends MutableMap<K,V>, UnmodifiableHashMap<K,V>
{
	interface Defaults<K, V> extends MutableHashMap<K,V>, UnmodifiableHashMap.Defaults<K,V>, MutableMap.Defaults<K,V>
	{
	}
}
