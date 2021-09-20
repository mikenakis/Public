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
	public enum Tag
	{
		Byte       /**/( 'B' ), //
		Character  /**/( 'C' ), //
		Double     /**/( 'D' ), //
		Float      /**/( 'F' ), //
		Integer    /**/( 'I' ), //
		Long       /**/( 'J' ), //
		Short      /**/( 'S' ), //
		Boolean    /**/( 'Z' ), //
		String     /**/( 's' ), //
		Annotation /**/( '@' ), //
		Array      /**/( '[' ), //
		Class      /**/( 'c' ), //
		Enum       /**/( 'e' );

		public final char character;

		Tag( char character )
		{
			this.character = character;
		}

		public static Tag fromCharacter( int character )
		{
			return switch( character )
				{
					case 'B' -> Byte;
					case 'C' -> Character;
					case 'I' -> Integer;
					case 'S' -> Short;
					case 'Z' -> Boolean;
					case 'J' -> Long;
					case 'D' -> Double;
					case 'F' -> Float;
					case 's' -> String;
					case '@' -> Annotation;
					case '[' -> Array;
					case 'c' -> Class;
					case 'e' -> Enum;
					default -> throw new AssertionError();
				};
		}
	}

	public final Tag tag;

	protected AnnotationValue( Tag tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ConstAnnotationValue      /**/ asConstAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnumAnnotationValue       /**/ asEnumAnnotationValue()       /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassAnnotationValue      /**/ asClassAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationAnnotationValue /**/ asAnnotationAnnotationValue() /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ArrayAnnotationValue      /**/ asArrayAnnotationValue()      /**/ { return Kit.fail(); }
}
