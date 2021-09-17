package mikenakis.bytecode.model.oracle;

public class ClassSignature implements Signature
{
	public static ClassSignature make( FormalTypeParameter[] ftps, ClassTypeSignature sc, ClassTypeSignature[] sis )
	{
		return new ClassSignature( ftps, sc, sis );
	}

	private final FormalTypeParameter[] formalTypeParams;
	private final ClassTypeSignature superclass;
	private final ClassTypeSignature[] superInterfaces;

	private ClassSignature( FormalTypeParameter[] ftps, ClassTypeSignature sc, ClassTypeSignature[] sis )
	{
		formalTypeParams = ftps;
		superclass = sc;
		superInterfaces = sis;
	}

	@Override public FormalTypeParameter[] getFormalTypeParameters() { return formalTypeParams; }
	public ClassTypeSignature getSuperclass() { return superclass; }
	public ClassTypeSignature[] getSuperInterfaces() { return superInterfaces; }
}
