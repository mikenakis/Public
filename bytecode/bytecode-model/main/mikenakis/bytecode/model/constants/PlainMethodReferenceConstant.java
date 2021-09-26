package mikenakis.bytecode.model.constants;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

/**
 * Represents the JVMS::CONSTANT_Methodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class PlainMethodReferenceConstant extends MethodReferenceConstant
{
	public static PlainMethodReferenceConstant of( ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		PlainMethodReferenceConstant plainMethodReferenceConstant = new PlainMethodReferenceConstant();
		plainMethodReferenceConstant.setDeclaringTypeConstant( declaringTypeConstant );
		plainMethodReferenceConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return plainMethodReferenceConstant;
	}

	public PlainMethodReferenceConstant()
	{
		super( tag_MethodReference );
	}

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: declaringTypeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodTypeDesc methodDescriptor() { return MethodTypeDesc.ofDescriptor( getNameAndDescriptorConstant().getDescriptorConstant().stringValue() ); }
	public ClassDesc owningClassDescriptor() { return getDeclaringTypeConstant().classDesc(); }
	public String methodName() { return getNameAndDescriptorConstant().getNameConstant().stringValue(); }

	@Deprecated @Override public PlainMethodReferenceConstant asPlainMethodReferenceConstant()
	{
		return this;
	}
}
