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
	public static PlainMethodReferenceConstant of( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return new PlainMethodReferenceConstant( typeConstant, nameAndDescriptorConstant );
	}

	private PlainMethodReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( tagMethodReference, typeConstant, nameAndDescriptorConstant );
	}

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: typeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodTypeDesc methodDescriptor() { return MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant.descriptorConstant.stringValue() ); }
	public ClassDesc owningClassDescriptor() { return typeConstant.classDescriptor(); }
	public String methodName() { return nameAndDescriptorConstant.nameConstant.stringValue(); }

	@Deprecated @Override public PlainMethodReferenceConstant asPlainMethodReferenceConstant()
	{
		return this;
	}
}
