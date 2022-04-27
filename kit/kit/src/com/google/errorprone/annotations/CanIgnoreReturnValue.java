package com.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can-Ignore-Return-Value annotation.
 *
 * WARNING: according to "errorprone", this annotation is only applicable if the annotated method is in a class annotated with javax.annotation.CheckReturnValue.
 *
 * @author michael.gr
 */
@Documented
@Retention( RetentionPolicy.CLASS )
@Target( { ElementType.METHOD, ElementType.TYPE } )
public @interface CanIgnoreReturnValue
{
}
