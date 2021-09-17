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
	private final NameAndTypeConstant nameAndTypeConstant;

	protected ReferenceConstant( int tag, ClassConstant typeConstant, NameAndTypeConstant nameAndTypeConstant )
	{
		super( tag );
		assert tag == FieldReferenceConstant.TAG || tag == PlainMethodReferenceConstant.TAG || tag == InterfaceMethodReferenceConstant.TAG;
		this.typeConstant = typeConstant;
		this.nameAndTypeConstant = nameAndTypeConstant;
	}

	public ClassConstant typeConstant() { return typeConstant; }
	public NameAndTypeConstant nameAndTypeConstant() { return nameAndTypeConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public final String toString()
	{
		return "type = " + typeConstant + ", nameAndType = " + nameAndTypeConstant;
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
		if( !nameAndTypeConstant.equalsNameAndTypeConstant( other.nameAndTypeConstant ) )
			return false;
		return true;
	}

	@Override public final int hashCode()
	{
		return Objects.hash( tag, typeConstant, nameAndTypeConstant );
	}
}
