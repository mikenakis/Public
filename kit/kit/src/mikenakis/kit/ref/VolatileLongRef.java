package mikenakis.kit.ref;

/**
 * A volatile reference to a {@code long}.
 *
 * @author michael.gr
 */
public final class VolatileLongRef
{
	public volatile long value;

	public VolatileLongRef( long value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
