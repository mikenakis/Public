package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.TypeDescriptor;

public final class MethodReference
{
	public static MethodReference of( TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		return new MethodReference( declaringTypeDescriptor, methodPrototype );
	}

	public final TypeDescriptor declaringTypeDescriptor;
	public final MethodPrototype methodPrototype;

	private MethodReference( TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		this.declaringTypeDescriptor = declaringTypeDescriptor;
		this.methodPrototype = methodPrototype;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( " " ).append( methodPrototype.descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( declaringTypeDescriptor.typeName() ).append( "." ).append( methodPrototype.methodName );
		methodPrototype.descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}
}
