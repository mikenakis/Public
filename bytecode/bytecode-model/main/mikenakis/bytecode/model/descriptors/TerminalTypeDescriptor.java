package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;

import java.util.Objects;

public class TerminalTypeDescriptor extends TypeDescriptor
{
	public static TerminalTypeDescriptor of( Class<?> javaClass )
	{
		assert !javaClass.isArray();
		assert !javaClass.isPrimitive();
		return ofTypeName( javaClass.getTypeName() );
	}

	public static TerminalTypeDescriptor ofTypeName( String typeName )
	{
		String internalName = ByteCodeHelpers.internalFromBinary( typeName );
		return ofInternalName( internalName );
	}

	public static TerminalTypeDescriptor ofInternalName( String internalName )
	{
		return new TerminalTypeDescriptor( internalName );
	}

	private final String internalName;

	private TerminalTypeDescriptor( String internalName )
	{
		assert ByteCodeHelpers.isValidInternalName( internalName );
		this.internalName = internalName;
	}

	public String internalName() { return internalName; }
	@Override public String typeName() { return ByteCodeHelpers.binaryFromInternal( internalName ); }
	@Deprecated @Override public boolean isArray() { return false; }
	@Deprecated @Override public boolean isPrimitive() { return false; }
	@Deprecated @Override public boolean isTerminal() {	return true; }
	@Deprecated @Override public TerminalTypeDescriptor asTerminalTypeDescriptor() { return this; }
	@Override public String descriptorString() { return "L" + internalName + ";"; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof TerminalTypeDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( TerminalTypeDescriptor other )
	{
		return internalName.equals( other.internalName );
	}

	@Override public int hashCode()
	{
		return Objects.hash( TerminalTypeDescriptor.class, internalName );
	}
}
