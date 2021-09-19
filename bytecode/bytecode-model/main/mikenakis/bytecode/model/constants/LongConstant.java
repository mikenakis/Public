package mikenakis.bytecode.model.constants;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Objects;

/**
 * Represents the JVMS::CONSTANT_Long_info structure.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LongConstant extends ValueConstant<Long>
{
	public static LongConstant of( long value )
	{
		return new LongConstant( value );
	}

	public static final int TAG = 5; // JVMS::CONSTANT_Long_info

	public final long value;

	private LongConstant( long value )
	{
		super( TAG );
		this.value = value;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return value + "L";
	}

	@Deprecated @Override public LongConstant asLongConstant()
	{
		return this;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof LongConstant otherLongConstant )
			return value == otherLongConstant.value;
		return false;
	}

	@Override public int hashCode()
	{
		return Objects.hash( tag, value );
	}

	@Deprecated @Override public Long value()
	{
		return value;
	}
}
