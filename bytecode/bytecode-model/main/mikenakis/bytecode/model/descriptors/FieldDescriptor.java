package mikenakis.bytecode.model.descriptors;

import java.lang.reflect.Field;

public class FieldDescriptor
{
	public static FieldDescriptor of( Field field )
	{
		return of( field.getType() );
	}

	public static FieldDescriptor of( Class<?> fieldType )
	{
		return of( TypeDescriptor.of( fieldType ) );
	}

	public static FieldDescriptor of( TypeDescriptor fieldTypeDescriptor )
	{
		return new FieldDescriptor( fieldTypeDescriptor );
	}

	public final TypeDescriptor typeDescriptor;

	private FieldDescriptor( TypeDescriptor typeDescriptor )
	{
		this.typeDescriptor = typeDescriptor;
	}

	public String asString() { return typeDescriptor.typeName(); }
	public String descriptorString() { return typeDescriptor.descriptorString(); }
}
