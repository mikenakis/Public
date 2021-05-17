package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSR 305 Check-Return-Value annotation.
 * see com.google.errorprone.annotations.CanIgnoreReturnValue
 *
 * @author Michael Belivanakis (michael.gr)
 */
@Documented
@Retention( RetentionPolicy.CLASS )
@Target( { ElementType.TYPE, ElementType.METHOD } )
public @interface CanIgnoreReturnValue
{
}
