package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;

public final class MethodPrototype
{
	public static MethodPrototype of( String name, MethodDescriptor descriptor )
	{
		return new MethodPrototype( name, descriptor );
	}

	public static MethodPrototype of( String name, TypeDescriptor returnType, TypeDescriptor... parameterTypes )
	{
		return new MethodPrototype( name, MethodDescriptor.of( returnType, parameterTypes ) );
	}

	public final String name;
	public final MethodDescriptor descriptor;

	private MethodPrototype( String name, MethodDescriptor descriptor )
	{
		this.name = name;
		this.descriptor = descriptor;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( descriptor.returnTypeDescriptor.typeName() );
		stringBuilder.append( " " ).append( name );
		descriptor.appendParameters( stringBuilder );
		return stringBuilder.toString();
	}
}
