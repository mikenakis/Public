package mikenakis.immutability.type.field.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a promise that a certain variable field will be written at most once. (And that it will never be read before it is written.)
 *
 * This is essentially a promise that despite the field being mutable, the containing class will never actually mutate the field, so that
 * the containing class can be assessed as immutable if it meets all other requirements for immutability.
 *
 * For example, in an implementation of class {@link java.lang.String} the cached hashCode field would be marked with this annotation,
 * thus allowing the string class to be assessed as immutable.
 *
 * Note that if a field is marked as {@link InvariableField}, this only promises that the field itself will never change; however, the immutability
 * of the object referenced by the field still has to be assessed before the field can be considered immutable.
 *
 * Also see jdk.internal.vm.annotation.Stable which gives a similar promise but for slightly different purposes. (Strangely enough, in
 * {@link java.lang.String} even though the character array is marked with jdk.internal.vm.annotation.Stable, the cached hashcode is not;
 * I do not know why.)
 *
 * @author michael.gr
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface InvariableField
{
}
