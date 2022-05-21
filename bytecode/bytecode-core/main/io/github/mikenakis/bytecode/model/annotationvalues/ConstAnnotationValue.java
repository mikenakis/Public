package io.github.mikenakis.bytecode.model.annotationvalues;

import io.github.mikenakis.bytecode.exceptions.InvalidConstAnnotationValueTagException;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.AnnotationValue;
import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.bytecode.model.constants.ValueConstant;
import io.github.mikenakis.bytecode.model.constants.value.IntegerValueConstant;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

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
