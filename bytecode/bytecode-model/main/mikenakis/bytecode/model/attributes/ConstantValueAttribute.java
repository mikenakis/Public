package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Attribute;
import mikenakis.bytecode.model.ByteCodeField;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the "ConstantValue" {@link Attribute} of a java class file.
 * <p>
 * According to JVMS Table 4.7-C, this attribute is applicable to:
 * <p>
 * {@link ByteCodeField}
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstantValueAttribute extends KnownAttribute
{
	public static ConstantValueAttribute of( ValueConstant<?> valueConstant )
	{
		return new ConstantValueAttribute( valueConstant );
	}

	public final ValueConstant<?> valueConstant;

	private ConstantValueAttribute( ValueConstant<?> valueConstant )
	{
		super( tagConstantValue );
		this.valueConstant = valueConstant;
	}

	@Deprecated @Override public ConstantValueAttribute asConstantValueAttribute()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return "value = " + valueConstant.toString();
	}
}
