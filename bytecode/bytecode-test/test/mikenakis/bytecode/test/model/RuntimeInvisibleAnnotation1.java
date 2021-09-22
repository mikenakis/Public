package mikenakis.bytecode.test.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( //
	{ //
		ElementType.TYPE, //
		ElementType.FIELD, //
		ElementType.METHOD, //
		ElementType.PARAMETER, //
		ElementType.CONSTRUCTOR, //
		ElementType.LOCAL_VARIABLE, //
		ElementType.ANNOTATION_TYPE
		//ElementType.PACKAGE,
		//ElementType.TYPE_PARAMETER,
		//ElementType.TYPE_USE,
		//ElementType.MODULE
	} )
@Retention( RetentionPolicy.CLASS )
public @interface RuntimeInvisibleAnnotation1
{
	int value() default 5;
}
