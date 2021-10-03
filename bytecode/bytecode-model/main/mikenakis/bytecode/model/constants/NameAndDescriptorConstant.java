package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.java_type_model.MethodDescriptor;
import mikenakis.java_type_model.TypeDescriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndDescriptorConstant extends Constant
{
	public static NameAndDescriptorConstant of( String name, TypeDescriptor typeDescriptor )
	{
		return of( Mutf8Constant.of( name ), Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromTypeDescriptor( typeDescriptor ) ) );
	}

	public static NameAndDescriptorConstant of( String name, MethodDescriptor methodDescriptor )
	{
		return of( Mutf8Constant.of( name ), Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromMethodDescriptor( methodDescriptor ) ) );
	}

	public static NameAndDescriptorConstant of( Mutf8Constant nameConstant, Mutf8Constant descriptorConstant ) //TODO: remove
	{
		NameAndDescriptorConstant nameAndDescriptorConstant = new NameAndDescriptorConstant();
		nameAndDescriptorConstant.setNameConstant( nameConstant );
		nameAndDescriptorConstant.setDescriptorConstant( descriptorConstant );
		return nameAndDescriptorConstant;
	}

	private Mutf8Constant nameConstant;
	private Mutf8Constant descriptorConstant;

	public NameAndDescriptorConstant()
	{
		super( tag_NameAndDescriptor );
	}

	public Mutf8Constant getNameConstant()
	{
		assert nameConstant != null;
		return nameConstant;
	}

	public void setNameConstant( Mutf8Constant nameConstant )
	{
		assert this.nameConstant == null;
		assert nameConstant != null;
		this.nameConstant = nameConstant;
	}

	public Mutf8Constant getDescriptorConstant()
	{
		assert descriptorConstant != null;
		return descriptorConstant;
	}

	public void setDescriptorConstant( Mutf8Constant descriptorConstant )
	{
		assert this.descriptorConstant == null;
		assert descriptorConstant != null;
		this.descriptorConstant = descriptorConstant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "name = " + nameConstant + ", descriptor = " + descriptorConstant; }
	@Deprecated @Override public NameAndDescriptorConstant asNameAndDescriptorConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof NameAndDescriptorConstant kin && equals( kin ); }
	@Override public int hashCode() { return Objects.hash( tag, nameConstant, descriptorConstant ); }

	public boolean equals( NameAndDescriptorConstant other )
	{
		if( !nameConstant.equalsMutf8Constant( other.nameConstant ) )
			return false;
		if( !descriptorConstant.equalsMutf8Constant( other.descriptorConstant ) )
			return false;
		return true;
	}
}
