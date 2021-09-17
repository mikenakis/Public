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

	public static final int TAG = 6; // JVMS::CONSTANT_Double_info
	public static final String tagName = "Double";

	public final double value;

	private DoubleConstant( double value )
	{
		super( TAG );
		this.value = value;
	}

	@Deprecated @Override public DoubleConstant asDoubleConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof DoubleConstant otherDoubleConstant )
			return equalsDoubleConstant( otherDoubleConstant );
		return false;
	}

	public boolean equalsDoubleConstant( DoubleConstant other )
	{
		return Double.doubleToLongBits( value ) == Double.doubleToLongBits( other.value );
	}

	@Override public Double value()
	{
		return value;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, value );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return String.valueOf( value );
	}
}
