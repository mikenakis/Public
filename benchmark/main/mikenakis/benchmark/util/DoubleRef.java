package mikenakis.benchmark.util;

/**
 * A mutable reference to a {@code double}.
 *
 * @author michael.gr
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
