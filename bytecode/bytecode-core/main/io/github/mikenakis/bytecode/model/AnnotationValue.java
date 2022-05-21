package io.github.mikenakis.bytecode.model;

import io.github.mikenakis.bytecode.exceptions.InvalidAnnotationValueTagException;
import io.github.mikenakis.bytecode.kit.BufferReader;
import io.github.mikenakis.bytecode.kit.BufferWriter;
import io.github.mikenakis.bytecode.model.annotationvalues.AnnotationAnnotationValue;
import io.github.mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import io.github.mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
import io.github.mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import io.github.mikenakis.bytecode.model.annotationvalues.EnumAnnotationValue;
import io.github.mikenakis.bytecode.reading.ReadingConstantPool;
import io.github.mikenakis.bytecode.writing.Interner;
import io.github.mikenakis.bytecode.writing.WritingConstantPool;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the element_value structure of JVMS 4.7.16.1.
 *
 * @author michael.gr
 */
public abstract class AnnotationValue
{
	public static AnnotationValue read( BufferReader bufferReader, ReadingConstantPool constantPool )
	{
		char annotationValueTag = (char)bufferReader.readUnsignedByte();
		return switch( annotationValueTag )
			{
				case AnnotationValue.tagBoolean, AnnotationValue.tagByte, AnnotationValue.tagCharacter, AnnotationValue.tagDouble, //
					AnnotationValue.tagFloat, AnnotationValue.tagInteger, AnnotationValue.tagLong, AnnotationValue.tagShort, //
					AnnotationValue.tagString -> ConstAnnotationValue.read( bufferReader, constantPool, annotationValueTag );
				case AnnotationValue.tagAnnotation -> AnnotationAnnotationValue.read( bufferReader, constantPool );
				case AnnotationValue.tagArray -> ArrayAnnotationValue.read( bufferReader, constantPool );
				case AnnotationValue.tagClass -> ClassAnnotationValue.read( bufferReader, constantPool );
				case AnnotationValue.tagEnum -> EnumAnnotationValue.read( bufferReader, constantPool );
				default -> throw new InvalidAnnotationValueTagException( annotationValueTag );
			};
	}

	public static final char tagAnnotation /**/ = '@';
	public static final char tagArray      /**/ = '[';
	public static final char tagBoolean    /**/ = 'Z';
	public static final char tagByte       /**/ = 'B';
	public static final char tagCharacter  /**/ = 'C';
	public static final char tagClass      /**/ = 'c';
	public static final char tagDouble     /**/ = 'D';
	public static final char tagEnum       /**/ = 'e';
	public static final char tagFloat      /**/ = 'F';
	public static final char tagInteger    /**/ = 'I';
	public static final char tagLong       /**/ = 'J';
	public static final char tagShort      /**/ = 'S';
	public static final char tagString     /**/ = 's';

	public static String tagName( char tag )
	{
		return switch( tag )
			{
				case tagAnnotation /**/ -> "Annotation";
				case tagArray      /**/ -> "Array";
				case tagBoolean    /**/ -> "Boolean";
				case tagByte       /**/ -> "Byte";
				case tagCharacter  /**/ -> "Character";
				case tagClass      /**/ -> "Class";
				case tagDouble     /**/ -> "Double";
				case tagEnum       /**/ -> "Enum";
				case tagFloat      /**/ -> "Float";
				case tagInteger    /**/ -> "Integer";
				case tagLong       /**/ -> "Long";
				case tagShort      /**/ -> "Short";
				case tagString     /**/ -> "String";
				default -> throw new InvalidAnnotationValueTagException( tag );
			};
	}

	public final char tag;

	protected AnnotationValue( char tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ConstAnnotationValue      /**/ asConstAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnumAnnotationValue       /**/ asEnumAnnotationValue()       /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassAnnotationValue      /**/ asClassAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationAnnotationValue /**/ asAnnotationAnnotationValue() /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ArrayAnnotationValue      /**/ asArrayAnnotationValue()      /**/ { return Kit.fail(); }

	public abstract void intern( Interner interner );
	public abstract void write( BufferWriter bufferWriter, WritingConstantPool constantPool );
}
