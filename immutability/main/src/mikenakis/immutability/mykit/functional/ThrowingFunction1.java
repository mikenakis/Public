package mikenakis.immutability.mykit.functional;

/**
 * A method which accepts one argument, returns a value, and declares a checked exception.
 *
 * @param <R> the type of the return value.
 * @param <P> the type of the parameter.
 * @param <E> the type of the checked exception that may be thrown.
 */
public interface ThrowingFunction1<R, P, E extends Throwable>
{
	R invoke( P p ) throws E;
}
