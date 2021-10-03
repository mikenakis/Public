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

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return "value = " + (valueConstant == null ? "(uninitialized)" : valueConstant.toString()); }
	@Deprecated @Override public StringConstant asStringConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof StringConstant kin && valueConstant.equalsMutf8Constant( kin.valueConstant ); }
	public boolean equals( StringConstant other ) { return valueConstant.equalsMutf8Constant( other.valueConstant ); }
	@Deprecated @Override public String value() { return valueConstant.toString(); }
	@Override public int hashCode() { return Objects.hash( tag, valueConstant ); }
}
