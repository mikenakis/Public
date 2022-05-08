package mikenakis.immutability.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a promise that a certain array field will be initialized at most once. (And that it will never be read before it is written.)
 *
 * This is essentially a promise that despite the array being mutable by nature, the containing class will never actually mutate the array, so that
 * the containing class can be assessed as immutable if it meets all other requirements for immutability.
 *
 * For example, in an implementation of class {@link String} the array of characters would be marked with this annotation, thus allowing the string class
 * to be assessed as immutable.
 *
 * Note that if an array is marked as {@link InvariableArray}, this only promises that the array itself will never change; however, the immutability
 * of each array element still has to be assessed before the array as a whole can be considered immutable.
 *
 * @author michael.gr
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface InvariableArray
{
}
