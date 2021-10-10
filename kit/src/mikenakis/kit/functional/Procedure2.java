package mikenakis.kit.functional;

/**
 * A procedure which accepts two arguments and does not return a value.
 * (Corresponds to Java's {@link java.util.function.BiConsumer}.)
 *
 * @param <P1> the type of the 1st parameter.
 * @param <P2> the type of the 2nd parameter.
 *
 * @author michael.gr
 */
public interface Procedure2<P1, P2>
{
	void invoke( P1 parameter1, P2 parameter2 );
}
