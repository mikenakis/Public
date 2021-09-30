package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;

public abstract class TypeDescriptor extends Descriptor
{
	public static TypeDescriptor of( Class<?> javaClass )
	{
		if( javaClass.isArray() )
		{
			Class<?> componentJavaClass = javaClass.componentType();
			TypeDescriptor componentTypeDescriptor = of( componentJavaClass );
			return ArrayTypeDescriptor.of( componentTypeDescriptor );
		}
		return ofDescriptorString( javaClass.descriptorString() );
	}

	public static TypeDescriptor ofDescriptorString( String descriptorString )
	{
		return ByteCodeHelpers.typeDescriptorFromDescriptorString( descriptorString );
	}

	public abstract String descriptorString();

	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
}
