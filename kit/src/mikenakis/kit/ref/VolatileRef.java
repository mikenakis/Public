package mikenakis.kit.ref;

/**
 * A volatile reference to an object.
 *
 * @param <T> the type of the object
 *
 * @author michael.gr
 */
public final class VolatileRef<T>
{
	public volatile T value;

	public VolatileRef( T value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
