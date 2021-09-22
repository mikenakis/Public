package mikenakis.bytecode.model.constants;

/**
 * Base class for representing the JVMS::CONSTANT_Methodref_info and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class MethodReferenceConstant extends ReferenceConstant
{
	protected MethodReferenceConstant( int tag, ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( tag, typeConstant, nameAndDescriptorConstant );
	}

	@Deprecated @Override public MethodReferenceConstant asMethodReferenceConstant()
	{
		return this;
	}
}
