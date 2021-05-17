package mikenakis.rumination.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type as a Ruminant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Ruminant
{
	boolean processed() default false;
	String ruminatorMethodName() default "onRuminationEvent";
}
