package io.github.mikenakis.bytecode.model.descriptors;

import io.github.mikenakis.java_type_model.FieldDescriptor;
import io.github.mikenakis.java_type_model.TypeDescriptor;

public final class FieldPrototype
{
	public static FieldPrototype of( String fieldName, Class<?> fieldType )
	{
		return of( fieldName, TypeDescriptor.of( fieldType ) );
	}

	public static FieldPrototype of( String fieldName, TypeDescriptor fieldType )
	{
		return of( fieldName, FieldDescriptor.of( fieldType ) );
	}

	public static FieldPrototype of( String fieldName, FieldDescriptor descriptor )
	{
		return new FieldPrototype( fieldName, descriptor );
	}

	public final String fieldName;
	public final FieldDescriptor descriptor;

	private FieldPrototype( String fieldName, FieldDescriptor descriptor )
	{
		this.fieldName = fieldName;
		this.descriptor = descriptor;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( descriptor.typeDescriptor.typeName() );
		stringBuilder.append( " " ).append( fieldName );
		return stringBuilder.toString();
	}
}
