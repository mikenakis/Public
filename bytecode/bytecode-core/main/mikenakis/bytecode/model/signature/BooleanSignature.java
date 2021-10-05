package mikenakis.bytecode.model.signature;

/**
 * AST that represents the type boolean.
 */
public class BooleanSignature implements PrimitiveTypeSignature
{
	public static final BooleanSignature instance = new BooleanSignature();

	private BooleanSignature() { }
}
