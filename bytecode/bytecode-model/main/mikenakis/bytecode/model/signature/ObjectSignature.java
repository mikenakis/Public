package mikenakis.bytecode.model.signature;

/**
 * Common superinterface for nodes that represent a (possibly generic) type.
 * Corresponds to the 'FieldTypeSignature' production in the JVMS section on signatures.
 */
public interface ObjectSignature extends TypeSignature /*BaseType*/, TypeArgument
{
}
