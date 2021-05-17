package mikenakis.kit.ref;

/**
 * A volatile reference to a {@code double}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class VolatileDoubleRef
{
	public volatile double value;

	public VolatileDoubleRef( double value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
