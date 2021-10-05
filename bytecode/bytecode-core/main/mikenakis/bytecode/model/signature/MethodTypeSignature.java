package mikenakis.bytecode.model.signature;

public class MethodTypeSignature implements Signature
{
	private final FormalTypeParameter[] formalTypeParams;
	private final TypeSignature[] parameterTypes;
	private final ReturnType returnType;
	private final ObjectSignature[] exceptionTypes;

	private MethodTypeSignature( FormalTypeParameter[] ftps, TypeSignature[] pts, ReturnType rt, ObjectSignature[] ets )
	{
		formalTypeParams = ftps;
		parameterTypes = pts;
		returnType = rt;
		exceptionTypes = ets;
	}

	public static MethodTypeSignature make( FormalTypeParameter[] ftps, TypeSignature[] pts, ReturnType rt, ObjectSignature[] ets )
	{
		return new MethodTypeSignature( ftps, pts, rt, ets );
	}

	@Override public FormalTypeParameter[] getFormalTypeParameters()
	{
		return formalTypeParams;
	}

	public TypeSignature[] getParameterTypes() { return parameterTypes; }
	public ReturnType getReturnType() { return returnType; }
	public ObjectSignature[] getExceptionTypes() { return exceptionTypes; }
}
