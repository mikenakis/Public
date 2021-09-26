package mikenakis.bytecode.model.descriptors;

import java.lang.constant.MethodTypeDesc;
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

	public final TypeDescriptor fieldTypeDescriptor;

	private FieldDescriptor( TypeDescriptor fieldTypeDescriptor )
	{
		this.fieldTypeDescriptor = fieldTypeDescriptor;
	}

	@Override public String name()
	{
		return name( "" );
	}

	public String name( String fieldName )
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( fieldTypeDescriptor.name() );
		if( !fieldName.isEmpty() )
			stringBuilder.append( " " ).append( fieldName );
		return stringBuilder.toString();
	}

	public boolean equalsMethodTypeDesc( MethodTypeDesc methodTypeDesc )
	{
		assert false; //todo
		return false;
	}
}
