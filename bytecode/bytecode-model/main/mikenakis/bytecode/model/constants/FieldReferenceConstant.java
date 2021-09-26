package mikenakis.bytecode.model.constants;

import java.lang.constant.ClassDesc;

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
	public ClassDesc fieldTypeDescriptor() { return ClassDesc.ofDescriptor( getNameAndDescriptorConstant().getDescriptorConstant().stringValue() ); }
	public ClassDesc declaringClassDescriptor() { return getDeclaringTypeConstant().classDesc(); }
	public String fieldName() { return getNameAndDescriptorConstant().getNameConstant().stringValue(); }

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
