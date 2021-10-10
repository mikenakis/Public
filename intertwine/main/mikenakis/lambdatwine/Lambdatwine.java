package mikenakis.lambdatwine;

/**
 * Creates entwiners and untwiners for a given functional interface.
 *
 * @param <T> the type of the interface.
 *
 * @author michael.gr
 */
public interface Lambdatwine<T>
{
	/**
	 * Gets the type of the interface.
	 */
	Class<T> interfaceType();

	/**
	 * Creates a new implementation of the target interface which delegates to the given implementation of {@link AnyLambda}.
	 */
	T newEntwiner( AnyLambda<T> exitPoint );

	/**
	 * Creates a new implementation of {@link AnyLambda} which delegates to the given implementation of the target interface.
	 */
	AnyLambda<T> newUntwiner( T exitPoint );
}
