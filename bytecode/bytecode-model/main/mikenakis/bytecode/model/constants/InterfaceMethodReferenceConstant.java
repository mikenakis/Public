package mikenakis.bytecode.model.constants;

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

	public static final int TAG = 11; // JVMS::CONSTANT_InterfaceMethodRef_info

	private InterfaceMethodReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( TAG, typeConstant, nameAndDescriptorConstant );
	}

	@Deprecated @Override public InterfaceMethodReferenceConstant asInterfaceMethodReferenceConstant()
	{
		return this;
	}
}
