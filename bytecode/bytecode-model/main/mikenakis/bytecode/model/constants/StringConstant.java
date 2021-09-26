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
	public static StringConstant of( String value ) //TODO remove
	{
		StringConstant stringConstant = new StringConstant();
		stringConstant.setValueConstant( Mutf8Constant.of( value ) );
		return stringConstant;
	}

	private Mutf8Constant valueConstant;

	public StringConstant()
	{
		super( tag_String );
	}

	public Mutf8Constant getValueConstant()
	{
		assert valueConstant != null;
		return valueConstant;
	}

	public void setValueConstant( Mutf8Constant valueConstant )
	{
		assert this.valueConstant == null;
		assert valueConstant != null;
		this.valueConstant = valueConstant;
	}

	public ConstantDesc constantDescriptor() { return valueConstant.stringValue(); }

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
