package mikenakis.tyraki;

/**
 * Freezable {@link MutableArrayMap}.
 *
 * @author michael.gr
 */
public interface FreezableArrayMap<K, V> extends MutableArrayMap<K,V>, Freezable
{
	/**
	 * Freezes the map, making it immutable.
	 *
	 * @return the map. (Fluent.)
	 */
	UnmodifiableArrayMap<K,V> frozen();

	interface Defaults<K, V> extends FreezableArrayMap<K,V>, MutableArrayMap.Defaults<K,V>, Freezable.Defaults
	{
	}
}
