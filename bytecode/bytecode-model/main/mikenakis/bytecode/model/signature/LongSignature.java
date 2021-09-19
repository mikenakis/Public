package mikenakis.bytecode.model.signature;

/**
 * AST that represents the type long.
 */
public class LongSignature implements PrimitiveTypeSignature
{
	public static final LongSignature instance = new LongSignature();

	private LongSignature() { }
}
