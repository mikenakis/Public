package mikenakis.tyraki;

/**
 * A binding between a key and a value.
 *
 * @param <K> the type of the key.
 * @param <V> the type of the value.
 *
 * @author michael.gr
 */
public interface Binding<K, V>
{
	K getKey();

	V getValue();
}
