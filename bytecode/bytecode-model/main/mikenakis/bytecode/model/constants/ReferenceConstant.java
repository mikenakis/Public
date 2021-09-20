package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Base class for representing the JVMS::CONSTANT_Fieldref_info, JVMS::CONSTANT_Methodref_info, and JVMS::CONSTANT_InterfaceMethodref_info structures.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ReferenceConstant extends Constant
{
	private final ClassConstant typeConstant;
	private final NameAndDescriptorConstant nameAndDescriptorConstant;

	protected ReferenceConstant( Tag tag, ClassConstant typeConstant, NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		super( tag );
		assert tag == Constant.Tag.FieldReference || tag == Constant.Tag.MethodReference || tag == Constant.Tag.InterfaceMethodReference;
		this.typeConstant = typeConstant;
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

	public ClassConstant typeConstant() { return typeConstant; }
	public NameAndDescriptorConstant nameAndDescriptorConstant() { return nameAndDescriptorConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public final String toString()
	{
		return "type = " + typeConstant + ", nameAndDescriptor = " + nameAndDescriptorConstant;
	}

	@Deprecated @Override public final ReferenceConstant asReferenceConstant()
	{
		return this;
	}

	@Override public final boolean equals( Object other )
	{
		if( other instanceof ReferenceConstant otherReferenceConstant )
			return equalsReferenceConstant( otherReferenceConstant );
		return false;
	}

	public boolean equalsReferenceConstant( ReferenceConstant other )
	{
		if( !typeConstant.equalsClassConstant( other.typeConstant ) )
			return false;
		if( !nameAndDescriptorConstant.equalsNameAndDescriptorConstant( other.nameAndDescriptorConstant ) )
			return false;
		return true;
	}

	@Override public final int hashCode()
	{
		return Objects.hash( tag, typeConstant, nameAndDescriptorConstant );
	}
}
