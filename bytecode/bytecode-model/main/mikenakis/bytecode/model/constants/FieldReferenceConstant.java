package mikenakis.bytecode.model.constants;

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

	public static final int TAG = 9; // JVMS::CONSTANT_Fieldref_info

	private FieldReferenceConstant( ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( TAG, typeConstant, nameAndDescriptorConstant );
	}

	@Deprecated @Override public FieldReferenceConstant asFieldReferenceConstant()
	{
		return this;
	}
}
