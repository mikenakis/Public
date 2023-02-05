package io.github.mikenakis.java_type_model;

/**
 * Represents a type.
 *
 * @author michael.gr
 */
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

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static TypeDescriptor of( String typeName )
	{
		return TerminalTypeDescriptor.of( typeName );
	}

	public abstract String typeName();
	public abstract boolean isArray();
	public abstract boolean isPrimitive();
	public abstract boolean isTerminal();
	public ArrayTypeDescriptor asArrayTypeDescriptor() { throw new AssertionError(); }
	public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { throw new AssertionError(); }
	public TerminalTypeDescriptor asTerminalTypeDescriptor() { throw new AssertionError(); }
	@Override public abstract boolean equals( Object other );
	@Override public abstract int hashCode();
}
