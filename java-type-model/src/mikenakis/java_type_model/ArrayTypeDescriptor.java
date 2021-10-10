package mikenakis.java_type_model;

import java.util.Objects;

/**
 * Represents an array type.
 *
 * @author michael.gr
 */
public final class ArrayTypeDescriptor extends TypeDescriptor
{
	public static ArrayTypeDescriptor ofArray( Class<?> arrayClass )
	{
		assert arrayClass.isArray();
		return ofComponent( arrayClass.componentType() );
	}

	public static ArrayTypeDescriptor ofComponent( Class<?> componentClass )
	{
		TypeDescriptor componentTypeDescriptor = TypeDescriptor.of( componentClass );
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	public static ArrayTypeDescriptor ofComponent( TypeDescriptor componentTypeDescriptor )
	{
		return new ArrayTypeDescriptor( componentTypeDescriptor );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public final TypeDescriptor componentTypeDescriptor;

	private ArrayTypeDescriptor( TypeDescriptor componentTypeDescriptor )
	{
		this.componentTypeDescriptor = componentTypeDescriptor;
	}

	@Override public String typeName() { return componentTypeDescriptor.typeName() + "[]"; }
	@Deprecated @Override public boolean isArray() { return true; }
	@Deprecated @Override public boolean isPrimitive() { return false; }
	@Deprecated @Override public boolean isTerminal() { return false; }
	@Deprecated @Override public ArrayTypeDescriptor asArrayTypeDescriptor() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof ArrayTypeDescriptor kin && equals( kin ); }
	public boolean equals( ArrayTypeDescriptor other ) { return componentTypeDescriptor.equals( other.componentTypeDescriptor ); }
	@Override public int hashCode() { return Objects.hash( ArrayTypeDescriptor.class.hashCode(), componentTypeDescriptor.hashCode() ); }
}
