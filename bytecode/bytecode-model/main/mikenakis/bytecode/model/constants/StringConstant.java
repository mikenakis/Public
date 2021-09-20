package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
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

	private final Mutf8Constant valueConstant;

	private StringConstant( Mutf8Constant value )
	{
		super( Tag.String );
		valueConstant = value;
	}

	public Mutf8Constant valueConstant() { return valueConstant; }
	@Override public ConstantDesc constantDescriptor() { return valueConstant.stringValue(); }

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
