package io.github.mikenakis.kit.functional;

/**
 * A method which accepts two arguments and returns a value.
 * (Corresponds to Java's {@link java.util.function.BiFunction}, except that the return type is the first generic argument.)
 *
 * @param <R>  the type of the return value.
 * @param <P1> the type of the 1st parameter.
 * @param <P2> the type of the 2nd parameter.
 *
 * @author michael.gr
 */
public interface Function2<R, P1, P2>
{
	R invoke( P1 parameter1, P2 parameter2 );
}
