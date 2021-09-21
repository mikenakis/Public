package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.ElementValue;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a constant {@link ElementValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstElementValue extends ElementValue
{
	public static ConstElementValue of( Tag tag, ValueConstant<?> valueConstant )
	{
		return new ConstElementValue( tag, valueConstant );
	}

	public static ConstElementValue of( boolean value )
	{
		IntegerConstant valueConstant = IntegerConstant.of( value ? 1 : 0 );
		return of( Tag.Boolean, valueConstant );
	}

	private final ValueConstant<?> valueConstant;

	private ConstElementValue( Tag tag, ValueConstant<?> valueConstant )
	{
		super( tag );
		assert valueConstant.tag == getConstantTagFromValueTag( tag );
		this.valueConstant = valueConstant;
	}

	private static Constant.Tag getConstantTagFromValueTag( Tag tag )
	{
		return switch( tag )
			{
				case Byte, Character, Integer, Short, Boolean -> Constant.Tag.Integer;
				case Long -> /* long */ Constant.Tag.Long;
				case Double -> /* double */ Constant.Tag.Double;
				case Float -> /* float */ Constant.Tag.Float;
				case String -> /* string */ Constant.Tag.Mutf8;
				default -> throw new AssertionError();
			};
	}

	public ValueConstant<?> valueConstant() { return valueConstant; }

	@Deprecated @Override public ConstElementValue asConstAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return tag.name() + " value = " + valueConstant;
	}
}
