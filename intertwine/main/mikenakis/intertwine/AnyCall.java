package mikenakis.intertwine;

/**
 * Represents any interface.
 *
 * @param <T> the type of the interface being represented.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface AnyCall<T>
{
	/**
	 * Invokes a method.
	 *
	 * @param key       identifies the method being invoked.
	 * @param arguments the arguments passed to the method.
	 *
	 * @return the return value of the method; {@code null} if the method is of {@code void} return type.
	 */
	Object anyCall( Intertwine.Key<T> key, Object[] arguments );
}
