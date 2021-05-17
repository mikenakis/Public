package mikenakis.kit.functional;

/**
 * A method which accepts no arguments, does not return a value, and declares a checked exception.
 *
 * @param <E> the type of the checked exception that may be thrown.
 *
 * @author Mike Nakis (michael.gr)
 */
public interface ThrowingProcedure0<E extends Throwable>
{
	void invoke() throws E;
}
