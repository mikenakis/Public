package mikenakis.kit.ref;

/**
 * A mutable reference to a {@code double}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class DoubleRef
{
	public double value;

	public DoubleRef( double value )
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return " -> " + value;
	}
}
