package javax.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overriding-Methods-Must-Invoke-Super annotation.
 * <p>
 * See JSR 305: Annotations for Software Defect Detection (<a href="https://jcp.org/en/jsr/detail?id=305">https://jcp.org/en/jsr/detail?id=305</a>)
 *
 * @author michael.gr
 */
@Retention( RetentionPolicy.CLASS )
@Target( ElementType.METHOD )
public @interface OverridingMethodsMustInvokeSuper
{
}
