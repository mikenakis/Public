package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;

public class MethodPrototype
{
	public static MethodPrototype of( String name, MethodDescriptor descriptor )
	{
		return new MethodPrototype( name, descriptor );
	}

	public static MethodPrototype of( String name, TypeDescriptor returnType, TypeDescriptor... parameterTypes )
	{
		return new MethodPrototype( name, MethodDescriptor.of( returnType, parameterTypes ) );
	}

	public static MethodPrototype of( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		String name = nameAndDescriptorConstant.getNameConstant().stringValue();
		MethodDescriptor descriptor = MethodDescriptor.ofDescriptorString( nameAndDescriptorConstant.getDescriptorConstant().stringValue() );
		return of( name, descriptor );
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
