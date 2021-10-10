package mikenakis.kit.ref;

/**
 * A mutable reference to an {@code int}.
 *
 * @author michael.gr
 */
public class IntRef
{
	public int value;

	public IntRef( int value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
