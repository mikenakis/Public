package mikenakis.immutability.internal.mykit.functional;

/**
 * A method which accepts one argument and returns a value.
 * (Corresponds to Java's {@link java.util.function.Function}, except that the return value is the first generic argument.)
 *
 * @param <R>  the type of the return value.
 * @param <P> the type of the parameter.
 *
 * @author michael.gr
 */
public interface Function1<R, P>
{
	static <X> Function1<X,X> identity()
	{
		return x -> x;
	}

	R invoke( P parameter );
}
