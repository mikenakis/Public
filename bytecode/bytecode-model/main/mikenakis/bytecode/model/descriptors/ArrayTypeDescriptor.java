package mikenakis.bytecode.model.descriptors;

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

	public final TypeDescriptor componentTypeDescriptor;

	private ArrayTypeDescriptor( TypeDescriptor componentTypeDescriptor )
	{
		this.componentTypeDescriptor = componentTypeDescriptor;
	}

	@Override public String name()
	{
		return componentTypeDescriptor.name() + "[]";
	}

	@Override public String descriptorString()
	{
		return "[" + componentTypeDescriptor.descriptorString();
	}

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
