package mikenakis.java_type_model;

import mikenakis.kit.Kit;

public abstract class TypeDescriptor
{
	public static TypeDescriptor of( Class<?> javaClass )
	{
		if( javaClass.isPrimitive() )
			return PrimitiveTypeDescriptor.of( javaClass );
		if( javaClass.isArray() )
			return ArrayTypeDescriptor.ofArray( javaClass );
		return TerminalTypeDescriptor.of( javaClass );
	}

	public abstract String typeName();
	public abstract boolean isArray();
	public abstract boolean isPrimitive();
	public abstract boolean isTerminal();
	public ArrayTypeDescriptor asArrayTypeDescriptor() { return Kit.fail(); }
	public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { return Kit.fail(); }
	public TerminalTypeDescriptor asTerminalTypeDescriptor() { return Kit.fail(); }

	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
}
