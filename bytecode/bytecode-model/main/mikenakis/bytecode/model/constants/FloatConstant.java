package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Float_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class FloatConstant extends ValueConstant<Float>
{
	public static FloatConstant of( float value )
	{
		return new FloatConstant( value );
	}

	public static final int TAG = 4; // JVMS::CONSTANT_Float_info

	public final float value;

	private FloatConstant( float value )
	{
		super( TAG );
		this.value = value;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return value + "f";
	}

	@Deprecated @Override public FloatConstant asFloatConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof FloatConstant otherFloatConstant )
			return Float.floatToIntBits( value ) == Float.floatToIntBits( otherFloatConstant.value );
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, value );
	}

	@Deprecated @Override public Float value()
	{
		return value;
	}
}
