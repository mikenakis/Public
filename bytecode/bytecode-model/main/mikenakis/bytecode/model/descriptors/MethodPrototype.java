package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;

public final class MethodPrototype
{
	public static MethodPrototype of( String methodName, MethodDescriptor descriptor )
	{
		return new MethodPrototype( methodName, descriptor );
	}

	public static MethodPrototype of( String methodName, TypeDescriptor returnType, TypeDescriptor... parameterTypes )
	{
		return new MethodPrototype( methodName, MethodDescriptor.of( returnType, parameterTypes ) );
	}

	public final String methodName;
	public final MethodDescriptor descriptor;

	private MethodPrototype( String methodName, MethodDescriptor descriptor )
	{
		this.methodName = methodName;
		this.descriptor = descriptor;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( methodName );
		descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}
}
