package mikenakis.lambdatwine;

/**
 * Represents any lambda.
 *
 * @param <T> the type of the lambda being represented.
 *
 * @author michael.gr
 */
public interface AnyLambda<T>
{
	/**
	 * Invokes the target lambda.
	 *
	 * @param arguments the arguments passed to the method.
	 *
	 * @return the return value of the method.  If the method is of {@code void} return type, {@code null} is returned.
	 *
	 * @throws RuntimeException or any checked exception thrown by the target method.
	 */
	Object anyLambda( Object[] arguments );
}
