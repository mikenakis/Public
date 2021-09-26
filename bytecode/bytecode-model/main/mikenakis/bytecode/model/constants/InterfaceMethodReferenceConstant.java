package mikenakis.bytecode.model.constants;

import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;

/**
 * Represents the JVMS::CONSTANT_InterfaceMethodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InterfaceMethodReferenceConstant extends MethodReferenceConstant
{
	public static InterfaceMethodReferenceConstant of( ClassConstant declaringTypeConstant, NameAndDescriptorConstant nameAndDescriptorConstant ) //TODO remove
	{
		InterfaceMethodReferenceConstant interfaceMethodReferenceConstant = new InterfaceMethodReferenceConstant();
		interfaceMethodReferenceConstant.setDeclaringTypeConstant( declaringTypeConstant );
		interfaceMethodReferenceConstant.setNameAndDescriptorConstant( nameAndDescriptorConstant );
		return interfaceMethodReferenceConstant;
	}

	public InterfaceMethodReferenceConstant()
	{
		super( tag_InterfaceMethodReference );
	}

	// String Customer.name( arguments );
	//  (1)     (2)     (3)     (1)
	// 1: nameAndDescriptorConstant.descriptorConstant
	// 2: declaringTypeConstant
	// 3: nameAndDescriptorConstant.nameConstant
	public MethodTypeDesc methodDescriptor() { return MethodTypeDesc.ofDescriptor( getNameAndDescriptorConstant().getDescriptorConstant().stringValue() ); }
	public ClassDesc owningClassDescriptor() { return getDeclaringTypeConstant().classDesc(); }
	public String methodName() { return getNameAndDescriptorConstant().getNameConstant().stringValue(); }

	@Deprecated @Override public InterfaceMethodReferenceConstant asInterfaceMethodReferenceConstant()
	{
		return this;
	}
}
