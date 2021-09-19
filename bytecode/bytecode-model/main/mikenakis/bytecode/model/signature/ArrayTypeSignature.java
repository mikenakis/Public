package mikenakis.bytecode.model.signature;

public class ArrayTypeSignature implements ObjectSignature
{
	private final TypeSignature componentType;

	private ArrayTypeSignature( TypeSignature ct ) { componentType = ct; }

	public static ArrayTypeSignature make( TypeSignature ct )
	{
		return new ArrayTypeSignature( ct );
	}

	public TypeSignature getComponentType() { return componentType; }
}
