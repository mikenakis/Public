package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.TypeDescriptor;

public final class MethodReference
{
	public static MethodReference of( MethodReferenceKind kind, Class<?> declaringType, MethodPrototype methodPrototype )
	{
		return of( kind, TypeDescriptor.of( declaringType ), methodPrototype );
	}

	public static MethodReference of( MethodReferenceKind kind, TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		return new MethodReference( kind, declaringTypeDescriptor, methodPrototype );
	}

	public final MethodReferenceKind kind;
	public final TypeDescriptor declaringTypeDescriptor;
	public final MethodPrototype methodPrototype;

	private MethodReference( MethodReferenceKind kind, TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		this.kind = kind;
		this.declaringTypeDescriptor = declaringTypeDescriptor;
		this.methodPrototype = methodPrototype;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( kind );
		stringBuilder.append( "; " ).append( methodPrototype.descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( declaringTypeDescriptor.typeName() ).append( "." ).append( methodPrototype.methodName );
		methodPrototype.descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}
}
