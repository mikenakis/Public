package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.reading.AttributeReader;
import mikenakis.bytecode.writing.ConstantWriter;
import mikenakis.bytecode.writing.Interner;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a constant {@link AnnotationValue}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ConstAnnotationValue extends AnnotationValue
{
	public static ConstAnnotationValue read( AttributeReader annotationReader,  char annotationValueTag )
	{
		Constant constant = annotationReader.readIndexAndGetConstant();
		ValueConstant valueConstant = constant.asValueConstant();
		return of( annotationValueTag, valueConstant );
	}

	public static ConstAnnotationValue of( char tag, ValueConstant valueConstant )
	{
		return new ConstAnnotationValue( tag, valueConstant );
	}

	public static ConstAnnotationValue of( boolean value )
	{
		IntegerValueConstant valueConstant = IntegerValueConstant.of( value ? 1 : 0 );
		return of( tagBoolean, valueConstant );
	}

	public final ValueConstant valueConstant;

	private ConstAnnotationValue( char tag, ValueConstant valueConstant )
	{
		super( tag );
		assert valueConstant.tag == getConstantTagFromValueTag( tag );
		this.valueConstant = valueConstant;
	}

	@Deprecated @Override public ConstAnnotationValue asConstAnnotationValue() { return this; }
	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return tagName( tag ) + " value = " + valueConstant; }

	@Override public void intern( Interner interner )
	{
		valueConstant.intern( interner );
	}

	@Override public void write( ConstantWriter constantWriter )
	{
		constantWriter.writeUnsignedByte( tag );
		constantWriter.writeUnsignedShort( constantWriter.getConstantIndex( valueConstant ) );
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
}
