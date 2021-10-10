package mikenakis.bytecode.model.annotationvalues;

import mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import mikenakis.bytecode.kit.BufferReader;
import mikenakis.bytecode.kit.BufferWriter;
import mikenakis.bytecode.model.AnnotationValue;
import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import mikenakis.bytecode.reading.ReadingConstantPool;
import mikenakis.bytecode.writing.Interner;
import mikenakis.bytecode.writing.WritingConstantPool;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a constant {@link AnnotationValue}.
 *
 * @author michael.gr
 */
public final class ConstAnnotationValue extends AnnotationValue
{
	public static ConstAnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool, char annotationValueTag )
	{
		Constant constant = constantPool.getConstant( bufferReader.readUnsignedShort() );
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

	@Override public void write( BufferWriter bufferWriter, WritingConstantPool constantPool )
	{
		bufferWriter.writeUnsignedByte( tag );
		bufferWriter.writeUnsignedShort( constantPool.getConstantIndex( valueConstant ) );
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
