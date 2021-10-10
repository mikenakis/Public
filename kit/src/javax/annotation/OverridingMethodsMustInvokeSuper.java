package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overriding-Methods-Must-Invoke-Super annotation.
 *
 * See JSR 305: Annotations for Software Defect Detection (https://jcp.org/en/jsr/detail?id=305)
 *
 * @author michael.gr
 */
@Retention( RetentionPolicy.CLASS )
@Target( ElementType.METHOD )
public @interface OverridingMethodsMustInvokeSuper
{
}
