package javax.annotation;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JSR 305 Check-Return-Value annotation.
 *
 * @author Michael Belivanakis (michael.gr)
 * @see CanIgnoreReturnValue
 */
@Documented
@Retention( RetentionPolicy.CLASS )
@Target( { ElementType.TYPE, ElementType.METHOD } )
public @interface CheckReturnValue
{
}
