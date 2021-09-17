package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.ByteCodeHelpers;
import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
		return of( Utf8Constant.of( name ) );
	}

	public static ClassConstant of( Utf8Constant nameConstant )
	{
		return new ClassConstant( nameConstant );
	}

	public static final int TAG = 7; // JVMS::CONSTANT_Class_info
	public static final String tagName = "Class";

	private final Utf8Constant nameConstant;

	private ClassConstant( Utf8Constant nameConstant )
	{
		super( TAG );
		this.nameConstant = nameConstant;
	}

	public Utf8Constant nameConstant() { return nameConstant; }

//	@Override public void intern( ConstantPool constantPool )
//	{
//		nameConstant.intern( constantPool );
//		super.intern( constantPool );
//	}
//
//	@Override public void write( ConstantPool constantPool, BufferWriter bufferWriter )
//	{
//		nameConstant.writeIndex( constantPool, bufferWriter );
//	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClassName();
	}

	@Deprecated @Override public ClassConstant asClassConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof ClassConstant otherClassConstant )
			return equalsClassConstant( otherClassConstant );
		return false;
	}

	public boolean equalsClassConstant( ClassConstant other )
	{
		return nameConstant.equals( other.nameConstant );
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameConstant );
	}

	public String getClassName()
	{
		return ByteCodeHelpers.getJavaTypeNameFromJvmTypeName( nameConstant.value() );
	}
}
