package mikenakis.kit.ref;

/**
 * A mutable reference to a {@code long}.
 *
 * @author michael.gr
 */
public class LongRef
{
	public long value;

	public LongRef( long value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
