package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Double_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class DoubleConstant extends ValueConstant<Double>
{
	public static DoubleConstant of( double value )
	{
		return new DoubleConstant( value );
	}

	public final double value;

	private DoubleConstant( double value )
	{
		super( tag_Double );
		this.value = value;
	}

	@Deprecated @Override public DoubleConstant asDoubleConstant() { return this; }
	@Deprecated @Override public boolean equals( Object other ) { return other instanceof DoubleConstant kin && equalsDoubleConstant( kin ); }
	public boolean equalsDoubleConstant( DoubleConstant other ) { return Double.doubleToLongBits( value ) == Double.doubleToLongBits( other.value ); }
	@Deprecated @Override public Double value() { return value; }
	@Override public int hashCode() { return Objects.hash( tag, value ); }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return String.valueOf( value ); }
}
