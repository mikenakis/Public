package mikenakis.kit.ref;

/**
 * A mutable reference to a {@code boolean}.
 *
 * @author michael.gr
 */
public class BooleanRef
{
	public boolean value;

	public BooleanRef( boolean value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
