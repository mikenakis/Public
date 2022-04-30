package mikenakis.immutability.internal.mykit.functional;

/**
 * A method which accepts no arguments and returns a {@code boolean} value.
 * (Corresponds to Java's {@link java.util.function.BooleanSupplier}.)
 *
 * @author Mike Nakis (michael.gr)
 */
public interface BooleanFunction0
{
	boolean invoke();
}
