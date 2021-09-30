package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.constants.ClassConstant;

import java.util.Objects;

public class TerminalTypeDescriptor extends TypeDescriptor
{
	public static TerminalTypeDescriptor of( Class<?> javaClass )
	{
		assert !javaClass.isArray();
		return new TerminalTypeDescriptor( javaClass.descriptorString() );
	}

	public static TerminalTypeDescriptor ofTypeName( String typeName )
	{
		String descriptorString = descriptorStringFromTypeName( typeName );
		return new TerminalTypeDescriptor( descriptorString );
	}

	public static TerminalTypeDescriptor ofDescriptorString( String descriptorString )
	{
		return new TerminalTypeDescriptor( descriptorString );
	}

	public static TerminalTypeDescriptor of( ClassConstant classConstant )
	{
		return new TerminalTypeDescriptor( classConstant.descriptorString() );
	}

	private final String descriptorString;

	private TerminalTypeDescriptor( String descriptorString )
	{
		assert !ByteCodeHelpers.isArrayDescriptorString( descriptorString );
		this.descriptorString = descriptorString;
	}

	@Override public String descriptorString() { return descriptorString; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof TerminalTypeDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( TerminalTypeDescriptor other )
	{
		return descriptorString.equals( other.descriptorString );
	}

	@Override public int hashCode()
	{
		return Objects.hash( TerminalTypeDescriptor.class, descriptorString );
	}

	@Override public String name()
	{
		return ByteCodeHelpers.typeNameFromDescriptorString( descriptorString );
	}

	private static String descriptorStringFromTypeName( String typeName )
	{
		if( typeName.equals( "boolean" ) )
			return "Z";
		if( typeName.equals( "byte" ) )
			return "B";
		if( typeName.equals( "char" ) )
			return "C";
		if( typeName.equals( "double" ) )
			return "D";
		if( typeName.equals( "float" ) )
			return "F";
		if( typeName.equals( "integer" ) )
			return "I";
		if( typeName.equals( "long" ) )
			return "J";
		if( typeName.equals( "short" ) )
			return "S";
		return "L" + typeName.replace( '.', '/' ) + ";";
	}
}
