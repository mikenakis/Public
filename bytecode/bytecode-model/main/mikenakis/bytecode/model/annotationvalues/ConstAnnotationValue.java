package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.Utf8Constant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a constant {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstAnnotationValue extends AnnotationValue
{
	public static ConstAnnotationValue of( int tag, ValueConstant<?> valueConstant )
	{
		return new ConstAnnotationValue( tag, valueConstant );
	}

	public static ConstAnnotationValue of( boolean value )
	{
		IntegerConstant valueConstant = IntegerConstant.of( value ? 1 : 0 );
		return of( BooleanTag, valueConstant );
	}

	public static final String BYTE_NAME       /**/ = "Byte";
	public static final String CHARACTER_NAME  /**/ = "Character";
	public static final String DOUBLE_NAME     /**/ = "Double";
	public static final String FLOAT_NAME      /**/ = "Float";
	public static final String INT_NAME        /**/ = "Integer";
	public static final String LONG_NAME       /**/ = "Long";
	public static final String SHORT_NAME      /**/ = "Short";
	public static final String BOOLEAN_NAME    /**/ = "Boolean";
	public static final String STRING_NAME     /**/ = "String";

	private final ValueConstant<?> valueConstant;

	private ConstAnnotationValue( int tag, ValueConstant<?> valueConstant )
	{
		super( tag );
		assert isValidTag( tag );
		assert valueConstant.tag == getConstantTagFromValueTag( tag );
		this.valueConstant = valueConstant;
	}

	private static boolean isValidTag( int tag )
	{
		return switch( tag )
			{
				case ByteTag, CharacterTag, DoubleTag, FloatTag, IntTag, LongTag, ShortTag, BooleanTag, StringTag -> true;
				default -> false;
			};
	}

	private static int getConstantTagFromValueTag( int tag )
	{
		return switch( tag )
			{
				case 'B' /* byte */, 'C' /* char */, 'I' /* int */, 'S' /* short */, 'Z' -> /* boolean */ IntegerConstant.TAG;
				case 'J' -> /* long */ LongConstant.TAG;
				case 'D' -> /* double */ DoubleConstant.TAG;
				case 'F' -> /* float */ FloatConstant.TAG;
				case 's' -> /* string */ Utf8Constant.TAG;
				default -> throw new AssertionError();
			};
	}

	public ValueConstant<?> valueConstant() { return valueConstant; }

	@Deprecated @Override public ConstAnnotationValue asConstAnnotationValue()
	{
		return this;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return AnnotationValue.getNameFromTag( tag ) + " value = " + valueConstant;
	}
}
