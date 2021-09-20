package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.util.Comparator;
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
		return of( Mutf8Constant.of( name ) );
	}

	public static ClassConstant of( Mutf8Constant nameConstant )
	{
		return new ClassConstant( nameConstant );
	}

	private final Mutf8Constant nameConstant;

	private ClassConstant( Mutf8Constant nameConstant )
	{
		super( Tag.Class );
		this.nameConstant = nameConstant;
	}

	public Mutf8Constant nameConstant() { return nameConstant; }
	@Override public ConstantDesc constantDescriptor() { return classDescriptor(); }
	public ClassDesc classDescriptor() { return ClassDesc.ofDescriptor( getDescriptorString( nameConstant.stringValue() ) ); }

	@Override protected Comparator<? extends Constant> comparator()
	{
		return comparator;
	}

	public static final Comparator<ClassConstant> comparator = Comparator.comparing( ( ClassConstant o ) -> o.nameConstant );

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return nameConstant.stringValue();
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
		return nameConstant.equalsMutf8Constant( other.nameConstant );
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameConstant );
	}

	private static String getDescriptorString( String internalName )
	{
		if( internalName.startsWith( "[" ) )
			return internalName;
		if( !internalName.endsWith( ";" ) )
			return "L" + internalName + ";";
		return internalName;
	}
}
