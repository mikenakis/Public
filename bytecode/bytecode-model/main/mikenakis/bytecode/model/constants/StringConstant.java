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
		return of( Utf8Constant.of( value ) );
	}

	public static StringConstant of( Utf8Constant value )
	{
		return new StringConstant( value );
	}

	public static final int TAG = 8; // JVMS::CONSTANT_String_info
	public static final String tagName = "String";

	private final Utf8Constant valueUtf8Constant;

	private StringConstant( Utf8Constant value )
	{
		super( TAG );
		valueUtf8Constant = value;
	}

	public Utf8Constant valueUtf8Constant()
	{
		return valueUtf8Constant;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "utf8 = " + valueUtf8Constant.toString();
	}

	@Deprecated @Override public StringConstant asStringConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof StringConstant stringConstant )
			return valueUtf8Constant.equals( stringConstant.valueUtf8Constant );
		return false;
	}

	@Override public String value()
	{
		return valueUtf8Constant.toString();
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, valueUtf8Constant );
	}
}
