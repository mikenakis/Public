package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.descriptors.FieldPrototype;
import mikenakis.bytecode.model.descriptors.MethodPrototype;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndDescriptorConstant extends Constant
{
	public static NameAndDescriptorConstant of( FieldPrototype fieldPrototype )
	{
		return of( Mutf8Constant.of( fieldPrototype.fieldName ), //
			Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromTypeDescriptor( fieldPrototype.descriptor.typeDescriptor ) ) );
	}

	public static NameAndDescriptorConstant of( MethodPrototype methodPrototype )
	{
		return of( Mutf8Constant.of( methodPrototype.methodName ), //
			Mutf8Constant.of( ByteCodeHelpers.descriptorStringFromMethodDescriptor( methodPrototype.descriptor ) ) );
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
	public boolean equals( NameAndDescriptorConstant other ) { return nameConstant.equalsMutf8Constant( other.nameConstant ) && descriptorConstant.equalsMutf8Constant( other.descriptorConstant ); }
}
