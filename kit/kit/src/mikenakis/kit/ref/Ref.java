package mikenakis.kit.ref;

/**
 * A mutable reference to an object.
 *
 * @param <T> the type of the object.
 *
 * @author michael.gr
 */
public class Ref<T>
{
	public T value;

	public Ref( T value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
