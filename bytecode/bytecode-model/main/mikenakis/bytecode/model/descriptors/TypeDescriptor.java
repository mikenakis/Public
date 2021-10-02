package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.kit.Kit;

public abstract class TypeDescriptor
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

	public abstract String typeName();
	public abstract boolean isArray();
	public abstract boolean isPrimitive();
	public abstract boolean isTerminal();
	public ArrayTypeDescriptor asArrayTypeDescriptor() { return Kit.fail(); }
	public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { return Kit.fail(); }
	public TerminalTypeDescriptor asTerminalTypeDescriptor() { return Kit.fail(); }
	public abstract String descriptorString();

	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
}
