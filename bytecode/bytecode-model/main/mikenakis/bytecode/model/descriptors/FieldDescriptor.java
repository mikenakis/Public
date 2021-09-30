package mikenakis.bytecode.model.descriptors;

import java.lang.reflect.Field;

public class FieldDescriptor extends Descriptor
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

	@Override public String name()
	{
		return name( "" );
	}

	public String name( String fieldName )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( typeDescriptor.name() );
		if( !fieldName.isEmpty() )
			stringBuilder.append( " " ).append( fieldName );
		return stringBuilder.toString();
	}

	public String descriptorString()
	{
		return typeDescriptor.descriptorString();
	}
}
