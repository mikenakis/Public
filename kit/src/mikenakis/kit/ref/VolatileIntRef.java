package mikenakis.kit.ref;

/**
 * A volatile reference to an {@code int}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class VolatileIntRef
{
	public volatile int value;

	public VolatileIntRef( int value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
