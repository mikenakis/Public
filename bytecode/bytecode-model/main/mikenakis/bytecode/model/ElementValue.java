package mikenakis.bytecode.model;

import mikenakis.bytecode.model.annotationvalues.AnnotationElementValue;
import mikenakis.bytecode.model.annotationvalues.ArrayElementValue;
import mikenakis.bytecode.model.annotationvalues.ClassElementValue;
import mikenakis.bytecode.model.annotationvalues.ConstElementValue;
import mikenakis.bytecode.model.annotationvalues.EnumElementValue;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Represents the element_value structure of JVMS 4.7.16.1.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class ElementValue
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

	protected ElementValue( Tag tag )
	{
		this.tag = tag;
	}

	@ExcludeFromJacocoGeneratedReport public ConstElementValue      /**/ asConstAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public EnumElementValue       /**/ asEnumAnnotationValue()       /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassElementValue      /**/ asClassAnnotationValue()      /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public AnnotationElementValue /**/ asAnnotationAnnotationValue() /**/ { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ArrayElementValue      /**/ asArrayAnnotationValue()      /**/ { return Kit.fail(); }
}
