package mikenakis.immutability.internal.mykit.functional;

/**
 * A method which accepts one argument and does not return a value.
 * (Corresponds to Java's {@link java.util.function.Consumer}.)
 *
 * @param <P> the type of the parameter.
 *
 * @author michael.gr
 */
public interface Procedure1<P>
{
	Procedure1<?> EMPTY = object -> {};

	static <T> Procedure1<T> noOp()
	{
		@SuppressWarnings( "unchecked" ) Procedure1<T> result = (Procedure1<T>)EMPTY;
		return result;
	}

	void invoke( P parameter );
}
