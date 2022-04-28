package mikenakis.immutability.mykit.functional;

/**
 * A method which accepts one generic argument and returns a {@code boolean} value.
 *
 * @author Mike Nakis (michael.gr)
 */
public interface BooleanFunction1<P>
{
	boolean invoke( P value );
}
