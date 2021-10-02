package mikenakis.bytecode.model.descriptors;

public class MethodReference
{
	public enum Kind
	{
		Plain,
		Interface
	}

	public static MethodReference of( Kind kind, TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		return new MethodReference( kind, declaringTypeDescriptor, methodPrototype );
	}

	public final Kind kind;
	public final TypeDescriptor declaringTypeDescriptor;
	public final MethodPrototype methodPrototype;

	private MethodReference( Kind kind, TypeDescriptor declaringTypeDescriptor, MethodPrototype methodPrototype )
	{
		this.kind = kind;
		this.declaringTypeDescriptor = declaringTypeDescriptor;
		this.methodPrototype = methodPrototype;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( "[" ).append( kind ).append( "]" );
		stringBuilder.append( " " ).append( methodPrototype.descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( declaringTypeDescriptor.typeName() ).append( "." ).append( methodPrototype.name );
		methodPrototype.descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}
}
