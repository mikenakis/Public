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
	private ClassConstant declaringTypeConstant; //null means it has not been set yet.
	private NameAndDescriptorConstant nameAndDescriptorConstant; //null means it has not been set yet.

	protected ReferenceConstant( int tag )
	{
		super( tag );
		assert tag == Constant.tag_FieldReference || tag == Constant.tag_PlainMethodReference || tag == Constant.tag_InterfaceMethodReference;
	}

	public ClassConstant getDeclaringTypeConstant()
	{
		assert declaringTypeConstant != null;
		return declaringTypeConstant;
	}

	public void setDeclaringTypeConstant( ClassConstant declaringTypeConstant )
	{
		assert this.declaringTypeConstant == null;
		assert declaringTypeConstant != null;
		this.declaringTypeConstant = declaringTypeConstant;
	}

	public NameAndDescriptorConstant getNameAndDescriptorConstant()
	{
		assert nameAndDescriptorConstant != null;
		return nameAndDescriptorConstant;
	}

	public void setNameAndDescriptorConstant( NameAndDescriptorConstant nameAndDescriptorConstant )
	{
		assert this.nameAndDescriptorConstant == null;
		assert nameAndDescriptorConstant != null;
		this.nameAndDescriptorConstant = nameAndDescriptorConstant;
	}

	@Deprecated @Override public final ReferenceConstant asReferenceConstant() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public final String toString() { return "declaringType = " + declaringTypeConstant + ", nameAndDescriptor = " + nameAndDescriptorConstant; }
	@Deprecated @Override public final boolean equals( Object other ) { return other instanceof ReferenceConstant kin && equals( kin ); }
	public boolean equals( ReferenceConstant other ) { return declaringTypeConstant.equalsClassConstant( other.declaringTypeConstant ) && nameAndDescriptorConstant.equals( other.nameAndDescriptorConstant ); }
	@Override public final int hashCode() { return Objects.hash( tag, declaringTypeConstant, nameAndDescriptorConstant ); }
}
