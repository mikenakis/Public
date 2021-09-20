package mikenakis.bytecode.model.constants;

import java.lang.constant.ClassDesc;

/**
 * Represents the JVMS::CONSTANT_Fieldref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FieldReferenceConstant extends ReferenceConstant
{
	public static FieldReferenceConstant of( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return new FieldReferenceConstant( typeConstant, nameAndDescriptorConstant );
	}

	private FieldReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( Tag.FieldReference, typeConstant, nameAndDescriptorConstant );
	}

	// String Customer.name;
	//  (1)     (2)     (3)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: typeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public ClassDesc fieldTypeDescriptor() { return ClassDesc.ofDescriptor( nameAndDescriptorConstant().descriptorConstant().stringValue() ); }
	public ClassDesc owningClassDescriptor() { return typeConstant().classDescriptor();	}
	public String fieldName() {	return nameAndDescriptorConstant().nameConstant().stringValue(); }

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
