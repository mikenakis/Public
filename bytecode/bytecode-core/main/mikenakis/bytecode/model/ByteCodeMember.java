package mikenakis.bytecode.model;

/**
 * Represents a Member. (Field or Method.)
 *
 * @author michael.gr
 */
public abstract class ByteCodeMember
{
	protected ByteCodeMember()
	{
	}

	public abstract String name();
}
