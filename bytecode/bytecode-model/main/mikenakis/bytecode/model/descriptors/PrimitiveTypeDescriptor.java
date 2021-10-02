package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;

import java.util.Objects;

public class PrimitiveTypeDescriptor extends TypeDescriptor
{
	public static PrimitiveTypeDescriptor of( Class<?> javaClass )
	{
		assert javaClass.isPrimitive();
		return ofTypeName( javaClass.getTypeName() );
	}

	public static PrimitiveTypeDescriptor ofTypeName( String typeName )
	{
		String descriptorString = ByteCodeHelpers.descriptorStringFromTypeName( typeName );
		assert descriptorString.length() == 1;
		return ofDescriptorString( descriptorString );
	}

	public static PrimitiveTypeDescriptor ofDescriptorString( String descriptorString )
	{
		return new PrimitiveTypeDescriptor( descriptorString );
	}

	private final String descriptorString;

	private PrimitiveTypeDescriptor( String descriptorString )
	{
		assert ByteCodeHelpers.isValidPrimitiveDescriptorString( descriptorString );
		this.descriptorString = descriptorString;
	}

	@Override public String typeName() { return ByteCodeHelpers.typeNameFromDescriptorString( descriptorString ); }
	@Deprecated @Override public boolean isArray() { return false; }
	@Deprecated @Override public boolean isTerminal() {	return false; }
	@Deprecated @Override public boolean isPrimitive() { return true; }
	@Deprecated @Override public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { return this; }
	@Override public String descriptorString() { return descriptorString; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof PrimitiveTypeDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( PrimitiveTypeDescriptor other )
	{
		return descriptorString.equals( other.descriptorString );
	}

	@Override public int hashCode()
	{
		return Objects.hash( PrimitiveTypeDescriptor.class, descriptorString );
	}
}
