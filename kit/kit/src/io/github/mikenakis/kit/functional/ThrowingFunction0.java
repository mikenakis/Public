package io.github.mikenakis.kit.functional;

/**
 * A method which accepts no arguments, returns a value, and declares a checked exception.
 *
 * @param <R> the type of the return value.
 * @param <E> the type of the checked exception that may be thrown.
 *
 * @author Mike Nakis (michael.gr)
 */
public interface ThrowingFunction0<R, E extends Throwable>
{
	R invoke() throws E;
}
