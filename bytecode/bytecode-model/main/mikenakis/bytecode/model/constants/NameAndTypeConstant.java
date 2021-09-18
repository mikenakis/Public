package mikenakis.bytecode.model.constants;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.Descriptor;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_NameAndType_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class NameAndTypeConstant extends Constant
{
	public static NameAndTypeConstant of( Utf8Constant nameConstant, Utf8Constant descriptorConstant )
	{
		return new NameAndTypeConstant( nameConstant, descriptorConstant );
	}

	public static NameAndTypeConstant of( String name, Descriptor descriptor )
	{
		return of( Utf8Constant.of( name ), Utf8Constant.of( descriptor.stringValue() ) );
	}

	public static NameAndTypeConstant of( String name, String descriptor )
	{
		return of( Utf8Constant.of( name ), Utf8Constant.of( descriptor ) );
	}

	public static final int TAG = 12; // JVMS::CONSTANT_NameAndType_info
	public static final String tagName = "NameAndType";

	private final Utf8Constant nameConstant;
	private final Utf8Constant descriptorConstant;

	private NameAndTypeConstant( Utf8Constant nameConstant, Utf8Constant descriptorConstant )
	{
		super( TAG );
		this.nameConstant = nameConstant;
		this.descriptorConstant = descriptorConstant;
	}

	public Utf8Constant nameConstant() { return nameConstant; }
	public Utf8Constant descriptorConstant() { return descriptorConstant; }

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "name = " + nameConstant + ", descriptor = " + descriptorConstant;
	}

	@Deprecated @Override public NameAndTypeConstant asNameAndTypeConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof NameAndTypeConstant otherNameAndTypeConstant )
			return equalsNameAndTypeConstant( otherNameAndTypeConstant );
		return false;
	}

	public boolean equalsNameAndTypeConstant( NameAndTypeConstant other )
	{
		if( !nameConstant.equalsUtf8Constant( other.nameConstant ) )
			return false;
		if( !descriptorConstant.equalsUtf8Constant( other.descriptorConstant ) )
			return false;
		return true;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, nameConstant, descriptorConstant );
	}
}
