package mikenakis.lambdatwine;

/**
 * Obtains {@link Lambdatwine}s for functional interfaces.
 *
 * @author michael.gr
 */
public interface LambdatwineFactory
{
	/**
	 * Get a {@link Lambdatwine} for a given functional interface.
	 *
	 * @param interfaceType the {@link Class} of the interface.
	 * @param <T>           the type of the class of the interface.
	 *
	 * @return a newly created or previously cached {@link Lambdatwine} for the given lambda.
	 */
	<T> Lambdatwine<T> getLambdatwine( Class<T> interfaceType );
}
