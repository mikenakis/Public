package mikenakis.immutability.mykit.functional;

/**
 * A method which accepts three arguments and does not return a value.
 *
 * @param <P1> the type of the 1st parameter.
 * @param <P2> the type of the 2nd parameter.
 * @param <P3> the type of the 3rd parameter.
 *
 * @author michael.gr
 */
public interface Procedure3<P1, P2, P3>
{
	void invoke( P1 parameter1, P2 parameter2, P3 parameter3 );
}
