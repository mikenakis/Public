package mikenakis.bytecode.model.oracle;

/**
 * AST that represents the type long.
 */
public class LongSignature implements PrimitiveTypeSignature
{
	public static final LongSignature instance = new LongSignature();

	private LongSignature() { }
}
