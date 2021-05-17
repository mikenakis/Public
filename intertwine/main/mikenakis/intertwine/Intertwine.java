package mikenakis.intertwine;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates entwiners and untwiners for a given interface; provides mapping between method keys and method signatures.
 *
 * @param <T> the type of the interface.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public interface Intertwine<T>
{
	/**
	 * Represents a method of an interface.
	 *
	 * @author Michael Belivanakis (michael.gr)
	 */
	interface Key<T>
	{
		/**
		 * Gets the {@link Intertwine} that created this {@link Key}.
		 */
		Intertwine<T> getIntertwine();

		/**
		 * Gets the name of this key, uniquely identifying this key among all other keys of the {@link Intertwine}.
		 */
		String getSignatureString();
	}

	/**
	 * Gets the type of the interface.
	 */
	Class<? super T> interfaceType();

	/**
	 * Gets all the keys of the interface.
	 */
	Collection<Key<T>> keys();

	/**
	 * Obtains a key given an id. (Useful only if binary compatibility is guaranteed or it has somehow been negotiated.)
	 */
	Key<T> keyById( int id );

	/**
	 * Obtains a key given a signature string.
	 */
	Key<T> keyBySignatureString( String signatureString );

	/**
	 * Creates a new implementation of the target interface which delegates to the given implementation of {@link AnyCall}.
	 */
	T newEntwiner( AnyCall<T> exitPoint );

	/**
	 * Creates a new implementation of {@link AnyCall} which delegates to the given instance of the target interface.
	 */
	AnyCall<T> newUntwiner( T exitPoint );

	/**
	 * Builds a signature string from a method.
	 *
	 * @param method the method to get the signature string of.
	 *
	 * @return the signature string of the method.
	 */
	static String signatureString( Method method )
	{
		Collection<Class<?>> parameterTypes = List.of( method.getParameterTypes() );
		return parameterTypes.stream().map( Class::getCanonicalName ).collect( Collectors.joining( ",", method.getName() + "(", ")" ) );
	}
}
