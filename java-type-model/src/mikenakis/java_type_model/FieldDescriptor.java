package mikenakis.java_type_model;

import java.lang.reflect.Field;
import java.util.Objects;

public final class FieldDescriptor
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

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof FieldDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( FieldDescriptor other )
	{
		return typeDescriptor.equals( other.typeDescriptor );
	}

	@Override public int hashCode()
	{
		return Objects.hash( FieldDescriptor.class, typeDescriptor );
	}
}
