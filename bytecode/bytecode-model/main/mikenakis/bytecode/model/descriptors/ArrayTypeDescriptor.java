package mikenakis.bytecode.model.descriptors;

import mikenakis.bytecode.model.ByteCodeHelpers;

import java.lang.constant.ClassDesc;
import java.util.Objects;

public class ArrayTypeDescriptor extends TypeDescriptor
{
	public static ArrayTypeDescriptor ofArrayClass( Class<?> arrayClass )
	{
		assert arrayClass.isArray();
		return ofComponentClass( arrayClass.componentType() );
	}

	public static ArrayTypeDescriptor ofComponentClass( Class<?> componentClass )
	{
		TypeDescriptor componentTypeDescriptor = TypeDescriptor.of( componentClass );
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	public static ArrayTypeDescriptor of( TypeDescriptor componentTypeDescriptor )
	{
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	public static ArrayTypeDescriptor ofDescriptorString( String descriptorString )
	{
		assert ByteCodeHelpers.isValidDescriptorString( descriptorString );
		ClassDesc classDesc = ClassDesc.ofDescriptor( descriptorString );
		assert classDesc.isArray();
		TypeDescriptor componentType = TypeDescriptor.ofDescriptorString( classDesc.componentType().descriptorString() );
		return of( componentType );
	}

	public final TypeDescriptor componentTypeDescriptor;

	private ArrayTypeDescriptor( TypeDescriptor componentTypeDescriptor )
	{
		this.componentTypeDescriptor = componentTypeDescriptor;
	}

	@Override public String typeName() { return componentTypeDescriptor.typeName() + "[]"; }
	@Deprecated @Override public boolean isArray() { return true; }
	@Deprecated @Override public boolean isPrimitive() { return false; }
	@Deprecated @Override public boolean isTerminal() {	return false; }
	@Deprecated @Override public ArrayTypeDescriptor asArrayTypeDescriptor() { return this; }
	@Override public String descriptorString() { return "[" + componentTypeDescriptor.descriptorString(); }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof ArrayTypeDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( ArrayTypeDescriptor other )
	{
		return componentTypeDescriptor.equals( other.componentTypeDescriptor );
	}

	@Override public int hashCode()
	{
		return Objects.hash( ArrayTypeDescriptor.class.hashCode(), componentTypeDescriptor.hashCode() );
	}
}
