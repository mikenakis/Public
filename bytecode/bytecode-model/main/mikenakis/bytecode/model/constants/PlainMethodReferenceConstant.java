package mikenakis.bytecode.model.constants;

/**
 * Represents the JVMS::CONSTANT_Methodref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class PlainMethodReferenceConstant extends MethodReferenceConstant
{
	public static PlainMethodReferenceConstant of( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		return new PlainMethodReferenceConstant( typeConstant, nameAndTypeConstant );
	}

	public static final int TAG = 10; // JVMS::CONSTANT_MethodRef_info
	public static final String tagName = "PlainMethodReference";

	private PlainMethodReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( TAG, typeConstant, nameAndTypeConstant );
	}

	@Deprecated @Override public PlainMethodReferenceConstant asPlainMethodReferenceConstant()
	{
		return this;
	}
}
