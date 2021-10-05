package mikenakis.bytecode.model.signature;

/**
 * AST that represents the type char.
 */
public class CharSignature implements PrimitiveTypeSignature
{
	public static final CharSignature instance = new CharSignature();

	private CharSignature() { }
}
