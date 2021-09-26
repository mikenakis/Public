package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Class_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassConstant extends Constant
{
	public static ClassConstant of( String name )
	{
		ClassConstant classConstant = new ClassConstant();
		classConstant.setNameConstant( Mutf8Constant.of( name ) );
		return classConstant;
	}

	private Mutf8Constant nameConstant;

	public ClassConstant()
	{
		super( tag_Class );
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

	public String internalName() { return nameConstant.stringValue(); }
	public String descriptorString() { return ByteCodeHelpers.descriptorStringFromInternalName( internalName() ); }
	public ClassDesc classDesc() { return ClassDesc.ofDescriptor( descriptorString() ); }
	public String typeName() { return ByteCodeHelpers.typeNameFromClassDesc( classDesc() );	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return nameConstant.stringValue(); }

	@Deprecated @Override public ClassConstant asClassConstant() { return this; }

	@Override public boolean equals( Object other )
	{
		if( other instanceof ClassConstant otherClassConstant )
			return equalsClassConstant( otherClassConstant );
		return false;
	}

	public boolean equalsClassConstant( ClassConstant other )
	{
		return nameConstant.equalsMutf8Constant( other.nameConstant );
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameConstant );
	}
}
