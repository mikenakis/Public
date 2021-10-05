package mikenakis.bytecode.model.descriptors;

import mikenakis.java_type_model.TypeDescriptor;

public final class FieldReference
{
	public static FieldReference of( TypeDescriptor declaringTypeDescriptor, FieldPrototype fieldPrototype )
	{
		return new FieldReference( declaringTypeDescriptor, fieldPrototype );
	}

	public final TypeDescriptor declaringTypeDescriptor;
	public final FieldPrototype fieldPrototype;

	private FieldReference( TypeDescriptor declaringTypeDescriptor, FieldPrototype fieldPrototype )
	{
		this.declaringTypeDescriptor = declaringTypeDescriptor;
		this.fieldPrototype = fieldPrototype;
	}

	public String asString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( fieldPrototype.descriptor.typeDescriptor.typeName() );
		stringBuilder.append( " " ).append( declaringTypeDescriptor.typeName() ).append( "." ).append( fieldPrototype.fieldName );
		return stringBuilder.toString();
	}
}
