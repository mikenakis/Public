package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.descriptors.FieldDescriptor;
import mikenakis.bytecode.model.descriptors.TypeDescriptor;

/**
 * Represents the JVMS::CONSTANT_Fieldref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FieldReferenceConstant extends ReferenceConstant
{
	public static FieldReferenceConstant of( ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant ) //TODO remove
	{
		FieldReferenceConstant fieldReferenceConstant = new FieldReferenceConstant();
		fieldReferenceConstant.setDeclaringTypeConstant( declaringTypeConstant );
		fieldReferenceConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return fieldReferenceConstant;
	}

	public FieldReferenceConstant()
	{
		super( tag_FieldReference );
	}

	// String Customer.name;
	//  (1)     (2)     (3)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: typeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public String fieldName() { return getNameAndDescriptorConstant().getNameConstant().stringValue(); }
	public FieldDescriptor fieldDescriptor() { return FieldDescriptor.of( TypeDescriptor.ofDescriptorString( getNameAndDescriptorConstant().getDescriptorConstant().stringValue() ) ); }
	public TypeDescriptor declaringTypeDescriptor() { return TypeDescriptor.ofDescriptorString( getDeclaringTypeConstant().descriptorString() ); }

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
