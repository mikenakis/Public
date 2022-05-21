package io.github.mikenakis.kit.functional;

/**
 * A method which accepts five arguments and returns a value.
 *
 * @param <R>  the type of the return value.
 * @param <P1> the type of the 1st parameter.
 * @param <P2> the type of the 2nd parameter.
 * @param <P3> the type of the 3rd parameter.
 * @param <P4> the type of the 4th parameter.
 * @param <P5> the type of the 5th parameter.
 *
 * @author michael.gr
 */
public interface Function5<R, P1, P2, P3, P4, P5>
{
	R invoke( P1 parameter1, P2 parameter2, P3 parameter3, P4 parameter4, P5 parameter5 );
}
