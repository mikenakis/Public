package io.github.mikenakis.java_type_model;

import java.util.Objects;

/**
 * Represents a terminal type, that is, a type which is not a primitive and not an array.
 *
 * @author michael.gr
 */
public final class TerminalTypeDescriptor extends TypeDescriptor
{
	public static TerminalTypeDescriptor of( Class<?> javaClass )
	{
		assert !javaClass.isArray();
		assert !javaClass.isPrimitive();
		return of( javaClass.getTypeName() );
	}

	public static TerminalTypeDescriptor of( String typeName )
	{
		return new TerminalTypeDescriptor( typeName );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public final String typeName;

	private TerminalTypeDescriptor( String typeName )
	{
		assert isValidTerminalTypeName( typeName );
		this.typeName = typeName;
	}

	@Deprecated @Override public String typeName() { return typeName; }
	@Deprecated @Override public boolean isArray() { return false; }
	@Deprecated @Override public boolean isPrimitive() { return false; }
	@Deprecated @Override public boolean isTerminal() { return true; }
	@Deprecated @Override public TerminalTypeDescriptor asTerminalTypeDescriptor() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof TerminalTypeDescriptor kin && equals( kin ); }
	public boolean equals( TerminalTypeDescriptor other ) { return typeName.equals( other.typeName ); }
	@Override public int hashCode() { return Objects.hash( TerminalTypeDescriptor.class, typeName ); }

	private static boolean isValidTerminalTypeName( String typeName )
	{
		//TODO improve this.
		if( typeName.isEmpty() )
			return false;
		char c = typeName.charAt( 0 );
		if( c == '[' )
			return false;
		if( typeName.startsWith( "L" ) )
			return false;
		if( typeName.endsWith( ";" ) )
			return false;
		if( typeName.contains( "/" ) )
			return false;
		if( typeName.endsWith( "[]" ) )
			return false;
		return true;
	}
}
