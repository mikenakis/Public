package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a constant {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstAnnotationValue extends AnnotationValue
{
	public static ConstAnnotationValue of( char tag, ValueConstant<?> valueConstant )
	{
		return new ConstAnnotationValue( tag, valueConstant );
	}

	public static ConstAnnotationValue of( boolean value )
	{
		IntegerConstant valueConstant = IntegerConstant.of( value ? 1 : 0 );
		return of( tagBoolean, valueConstant );
	}

	public final ValueConstant<?> valueConstant;

	private ConstAnnotationValue( char tag, ValueConstant<?> valueConstant )
	{
		super( tag );
		assert valueConstant.tag == getConstantTagFromValueTag( tag );
		this.valueConstant = valueConstant;
	}

	private static int getConstantTagFromValueTag( char annotationValueTag )
	{
		return switch( annotationValueTag )
			{
				case tagByte, tagCharacter, tagInteger, tagShort, tagBoolean -> Constant.tag_Integer;
				case tagLong -> Constant.tag_Long;
				case tagDouble -> Constant.tag_Double;
				case tagFloat -> Constant.tag_Float;
				case tagString -> Constant.tag_Mutf8;
				default -> throw new InvalidConstAnnotationValueTagException( annotationValueTag );
			};
	}

	@Deprecated @Override public ConstAnnotationValue asConstAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return tagName( tag ) + " value = " + valueConstant;
	}
}
