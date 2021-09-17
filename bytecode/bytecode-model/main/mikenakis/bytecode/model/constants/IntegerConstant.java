package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Integer_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class IntegerConstant extends ValueConstant<Integer>
{
	public static IntegerConstant of( int value )
	{
		return new IntegerConstant( value );
	}

	public static final int TAG = 3; // JVMS::CONSTANT_Integer_info
	public static final String tagName = "Integer";

	public final int value;

	private IntegerConstant( int value )
	{
		super( TAG );
		this.value = value;
	}

	@Deprecated @Override public IntegerConstant asIntegerConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof IntegerConstant otherIntegerConstant )
			return value == otherIntegerConstant.value;
		return false;
	}

	@Override public Integer value()
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