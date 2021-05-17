package mikenakis.intertwine;

/**
 * Obtains {@link Intertwine}s for interfaces.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface IntertwineFactory
{
	/**
	 * Gets an {@link Intertwine} for a given interface.
	 *
	 * @param interfaceType the {@link Class} of the interface.
	 * @param <T>           the type of the class of the interface.
	 *
	 * @return an {@link Intertwine} for the given interface.
	 */
	<T> Intertwine<T> getIntertwine( Class<? super T> interfaceType );
}
