package mikenakis.immutability.internal.mykit.functional;

/**
 * A method which accepts no arguments and does not return a value.
 * (Corresponds to Java's {@link Runnable}.)
 *
 * @author michael.gr
 */
public interface Procedure0
{
	Procedure0 noOp = () -> {};

	void invoke();
}
