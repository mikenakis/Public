package mikenakis.bytecode.model.oracle;

/**
 * AST that represents the pseudo-type void.
 */
public class VoidDescriptor implements ReturnType
{
	public static final VoidDescriptor instance = new VoidDescriptor();

	private VoidDescriptor() { }
}
