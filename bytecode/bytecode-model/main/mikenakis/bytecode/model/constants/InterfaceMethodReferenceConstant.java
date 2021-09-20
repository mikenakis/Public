package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

/**
 * Represents the JVMS::CONSTANT_InterfaceMethodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InterfaceMethodReferenceConstant extends MethodReferenceConstant
{
	public static InterfaceMethodReferenceConstant of( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		return new InterfaceMethodReferenceConstant( typeConstant, nameAndDescriptorConstant );
	}

	private InterfaceMethodReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( Tag.InterfaceMethodReference, typeConstant, nameAndDescriptorConstant );
	}

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: typeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodTypeDesc methodDescriptor() { return MethodTypeDesc.ofDescriptor( nameAndDescriptorConstant().descriptorConstant().stringValue() ); }
	public ClassDesc owningClassDescriptor() { return typeConstant().classDescriptor(); }
	public String methodName() { return nameAndDescriptorConstant().nameConstant().stringValue(); }

	@Deprecated @Override public InterfaceMethodReferenceConstant asInterfaceMethodReferenceConstant()
	{
		return this;
	}
}
