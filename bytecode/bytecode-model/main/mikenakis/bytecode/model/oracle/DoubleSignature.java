package mikenakis.bytecode.model.oracle;

/**
 * AST that represents the type double.
 */
public class DoubleSignature implements PrimitiveTypeSignature
{
	public static final DoubleSignature instance = new DoubleSignature();

	private DoubleSignature() { }
}
