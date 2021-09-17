package mikenakis.bytecode.model.constants;

/**
 * Represents the JVMS::CONSTANT_Fieldref_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FieldReferenceConstant extends ReferenceConstant
{
	public static FieldReferenceConstant of( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		return new FieldReferenceConstant( typeConstant, nameAndTypeConstant );
	}

	public static final int TAG = 9; // JVMS::CONSTANT_Fieldref_info
	public static final String tagName = "FieldReference";

	private FieldReferenceConstant( ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( TAG, typeConstant, nameAndTypeConstant );
	}

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
