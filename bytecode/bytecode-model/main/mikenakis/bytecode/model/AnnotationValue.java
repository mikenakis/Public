package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.UnknownValueException;
import mikenakis.bytecode.model.annotationvalues.AnnotationAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ArrayAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ClassAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.ConstAnnotationValue;
import mikenakis.bytecode.model.annotationvalues.EnumAnnotationValue;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents a value of an annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class AnnotationValue
{
	public static final char ByteTag       /**/ = 'B';
	public static final char CharacterTag  /**/ = 'C';
	public static final char DoubleTag     /**/ = 'D';
	public static final char FloatTag      /**/ = 'F';
	public static final char IntTag        /**/ = 'I';
	public static final char LongTag       /**/ = 'J';
	public static final char ShortTag      /**/ = 'S';
	public static final char BooleanTag    /**/ = 'Z';
	public static final char StringTag     /**/ = 's';
	public static final char AnnotationTag /**/ = '@';
	public static final char ArrayTag      /**/ = '[';
	public static final char ClassTag      /**/ = 'c';
	public static final char EnumTag       /**/ = 'e';

	public static String getNameFromTag( int tag )
	{
		return switch( tag )
			{
				case ByteTag -> ConstAnnotationValue.BYTE_NAME;
				case CharacterTag -> ConstAnnotationValue.CHARACTER_NAME;
				case DoubleTag -> ConstAnnotationValue.DOUBLE_NAME;
				case FloatTag -> ConstAnnotationValue.FLOAT_NAME;
				case IntTag -> ConstAnnotationValue.INT_NAME;
				case LongTag -> ConstAnnotationValue.LONG_NAME;
				case ShortTag -> ConstAnnotationValue.SHORT_NAME;
				case BooleanTag -> ConstAnnotationValue.BOOLEAN_NAME;
				case StringTag -> ConstAnnotationValue.STRING_NAME;
				case EnumTag -> EnumAnnotationValue.NAME;
				case ClassTag -> ClassAnnotationValue.NAME;
				case AnnotationTag -> AnnotationAnnotationValue.NAME;
				case ArrayTag -> ArrayAnnotationValue.NAME;
				default -> throw new UnknownValueException( tag );
			};
	}

	public final int tag;

	protected AnnotationValue( int tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ConstAnnotationValue      /**/ asConstAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnumAnnotationValue       /**/ asEnumAnnotationValue()       /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassAnnotationValue      /**/ asClassAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationAnnotationValue /**/ asAnnotationAnnotationValue() /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ArrayAnnotationValue      /**/ asArrayAnnotationValue()      /**/ { return Kit.fail(); }
}
