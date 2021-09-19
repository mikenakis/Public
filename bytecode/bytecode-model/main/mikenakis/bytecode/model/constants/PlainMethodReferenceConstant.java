package mikenakis.bytecode.model.constants;

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

	public static final int TAG = 10; // JVMS::CONSTANT_MethodRef_info

	private PlainMethodReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( TAG, typeConstant, nameAndDescriptorConstant );
	}

	@Deprecated @Override public PlainMethodReferenceConstant asPlainMethodReferenceConstant()
	{
		return this;
	}
}
