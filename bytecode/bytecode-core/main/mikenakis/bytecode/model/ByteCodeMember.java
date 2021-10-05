package mikenakis.bytecode.model;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ByteCodeMember
{
	protected ByteCodeMember()
	{
	}

	public abstract String name();
}
