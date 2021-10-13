package mikenakis.tyraki;

/**
 * Freezable {@link MutableHashMap}.
 *
 * @author michael.gr
 */
public interface FreezableHashMap<K, V> extends MutableHashMap<K,V>, FreezableMap<K,V>
{
	/**
	 * Fixes the map, making it immutable.
	 *
	 * @return the map.  (Fluent.)
	 */
	@Override UnmodifiableHashMap<K,V> frozen();

	interface Defaults<K, V> extends FreezableHashMap<K,V>, MutableHashMap.Defaults<K,V>, FreezableMap.Defaults<K,V>
	{
	}
}
