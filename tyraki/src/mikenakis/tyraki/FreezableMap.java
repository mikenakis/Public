package mikenakis.tyraki;

/**
 * Freezable {@link MutableMap}.
 *
 * @author michael.gr
 */
public interface FreezableMap<K, V> extends MutableMap<K,V>, Freezable
{
	/**
	 * Fixes the map, making it immutable.
	 *
	 * @return the map.  (Fluent.)
	 */
	UnmodifiableMap<K,V> frozen();

	interface Defaults<K, V> extends FreezableMap<K,V>, MutableMap.Defaults<K,V>, Freezable.Defaults
	{
	}
}
