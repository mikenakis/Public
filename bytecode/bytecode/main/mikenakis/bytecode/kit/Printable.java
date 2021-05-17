package mikenakis.bytecode.kit;

/**
 * Something that can be converted to a string.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Printable
{
	protected Printable()
	{
	}

	@Override public final String toString()
	{
		var builder = new StringBuilder();
		toStringBuilder( builder );
		return builder.toString();
	}

	public abstract void toStringBuilder( StringBuilder builder );
}
