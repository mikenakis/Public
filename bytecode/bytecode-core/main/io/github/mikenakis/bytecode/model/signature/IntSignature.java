package io.github.mikenakis.bytecode.model.signature;

/**
 * AST that represents the type int.
 */
public class IntSignature implements PrimitiveTypeSignature
{
	public static final IntSignature instance = new IntSignature();

	private IntSignature() { }
}
