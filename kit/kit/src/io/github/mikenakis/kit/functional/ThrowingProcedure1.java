package io.github.mikenakis.kit.functional;

/**
 * A method which accepts one argument, does not return a value, and declares a checked exception.
 *
 * @param <P> the type of the parameter.
 * @param <E> the type of the checked exception that may be thrown.
 *
 * @author Mike Nakis (michael.gr)
 */
public interface ThrowingProcedure1<P, E extends Throwable>
{
	void invoke( P p ) throws E;
}
