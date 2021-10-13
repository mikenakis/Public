package mikenakis.tyraki;

/**
 * Mutable Array Map.
 *
 * @author michael.gr
 */
public interface MutableArrayMap<K, V> extends MutableMap<K,V>, UnmodifiableArrayMap<K,V>
{
	/**
	 * Gets all entries.
	 *
	 * @return a {@link MutableList} of all entries.
	 */
	@Override MutableList<Binding<K,V>> mutableEntries();

	/**
	 * Gets a {@link MutableList} of all keys.
	 *
	 * @return an {@link UnmodifiableList} of all keys.
	 */
	@Override MutableArraySet<K> mutableKeys();

	/**
	 * Gets an {@link MutableList} of all values.
	 *
	 * @return an {@link UnmodifiableList} of all values.
	 */
	@Override MutableList<V> mutableValues();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	interface Defaults<K, V> extends MutableArrayMap<K,V>, MutableMap.Defaults<K,V>, UnmodifiableArrayMap.Defaults<K,V>
	{
		@Override default UnmodifiableList<Binding<K,V>> entries()
		{
			return mutableEntries();
		}

		@Override default UnmodifiableArraySet<K> keys()
		{
			return mutableKeys();
		}

		@Override default UnmodifiableList<V> values()
		{
			return mutableValues();
		}
	}
}
