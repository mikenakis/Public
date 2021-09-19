package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_String_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class StringConstant extends ValueConstant<String>
{
	public static StringConstant of( String value )
	{
		return of( Mutf8Constant.of( value ) );
	}

	public static StringConstant of( Mutf8Constant value )
	{
		return new StringConstant( value );
	}

	public static final int TAG = 8; // JVMS::CONSTANT_String_info

	private final Mutf8Constant valueConstant;

	private StringConstant( Mutf8Constant value )
	{
		super( TAG );
		valueConstant = value;
	}

	public Mutf8Constant valueConstant()
	{
		return valueConstant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "value = " + valueConstant.toString();
	}

	@Deprecated @Override public StringConstant asStringConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof StringConstant stringConstant )
			return valueConstant.equalsMutf8Constant( stringConstant.valueConstant );
		return false;
	}

	@Deprecated @Override public String value()
	{
		return valueConstant.toString();
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, valueConstant );
	}
}
