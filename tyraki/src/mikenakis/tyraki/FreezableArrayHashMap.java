package mikenakis.tyraki;

/**
 * Freezable {@link MutableArrayHashMap}.
 *
 * @author michael.gr
 */
public interface FreezableArrayHashMap<K, V> extends MutableArrayHashMap<K,V>, FreezableHashMap<K,V>, FreezableArrayMap<K,V>
{
	/**
	 * Freezes the map, making it immutable.
	 *
	 * @return the map. (Fluent.)
	 */
	@Override UnmodifiableArrayHashMap<K,V> frozen();

	interface Defaults<K, V> extends FreezableArrayHashMap<K,V>, MutableArrayHashMap.Defaults<K,V>, FreezableHashMap.Defaults<K,V>, FreezableArrayMap.Defaults<K,V>
	{
	}
}
