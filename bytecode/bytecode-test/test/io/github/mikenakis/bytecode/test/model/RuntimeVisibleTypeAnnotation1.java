package io.github.mikenakis.bytecode.test.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( //
	{ //
		//ElementType.TYPE,
		//ElementType.FIELD,
		//ElementType.METHOD,
		//ElementType.PARAMETER,
		//ElementType.CONSTRUCTOR,
		//ElementType.LOCAL_VARIABLE,
		//ElementType.ANNOTATION_TYPE,
		ElementType.PACKAGE, //
		ElementType.TYPE_PARAMETER, //
		ElementType.TYPE_USE, //
		ElementType.MODULE //
	} )
@Retention( RetentionPolicy.RUNTIME )
public @interface RuntimeVisibleTypeAnnotation1
{
	SuppressWarnings annotationParameter() default @SuppressWarnings( "" );

	String[] arrayParameter() default { "a", "b", "c" };

	boolean booleanParameter() default true;

	byte byteParameter() default 1;

	char charParameter() default 'c';

	double doubleParameter() default 0.5;

	float floatParameter() default 0.25f;

	int intParameter() default 2;

	long longParameter() default 3;

	short shortParameter() default 4;

	String stringParameter() default "x";

	Class<?> classParameter() default Object.class;

	int value() default 5;
}
