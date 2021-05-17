package mikenakis.kit.ref;

/**
 * A volatile reference to a {@code boolean}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class VolatileBooleanRef
{
	public volatile boolean value;

	public VolatileBooleanRef( boolean value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
