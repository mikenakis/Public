package mikenakis.bytecode.model.oracle;

/**
 * Common superinterface for generic signatures. These are the signatures
 * of complete class and method/constructor declarations.
 */
public interface Signature extends Tree
{
    FormalTypeParameter[] getFormalTypeParameters();
}
