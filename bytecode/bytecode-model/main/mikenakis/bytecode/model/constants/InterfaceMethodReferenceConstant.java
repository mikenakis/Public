package mikenakis.bytecode.model.constants;

/**
 * Represents the JVMS::CONSTANT_InterfaceMethodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InterfaceMethodReferenceConstant extends MethodReferenceConstant
{
	public static InterfaceMethodReferenceConstant of( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		return new InterfaceMethodReferenceConstant( typeConstant, nameAndTypeConstant );
	}

	public static final int TAG = 11; // JVMS::CONSTANT_InterfaceMethodRef_info
	public static final String tagName = "InterfaceMethodReference";

	private InterfaceMethodReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( TAG, typeConstant, nameAndTypeConstant );
	}

	@Deprecated @Override public InterfaceMethodReferenceConstant asInterfaceMethodReferenceConstant()
	{
		return this;
	}
}
