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
	public static <T> Ref<T> of( T value )
	{
		return new Ref<>( value );
	}

	public T value;

	private Ref( T value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
