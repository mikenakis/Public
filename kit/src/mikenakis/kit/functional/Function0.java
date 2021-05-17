package mikenakis.kit.functional;

/**
 * A method which accepts no arguments and returns a value.
 * (Corresponds to Java's {@link java.util.function.Supplier}.)
 *
 * @param <R> the type of the return value.
 *
 * @author Mike Nakis (michael.gr)
 */
public interface Function0<R>
{
	R invoke();
}
