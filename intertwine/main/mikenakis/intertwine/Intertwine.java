package mikenakis.intertwine;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates Entwiners and Untwiners for a given interface; provides mapping between method {@link Key}s indexes, and prototype strings.
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
		 * Gets the index of this {@link Key}.
		 */
		int getIndex();

		/**
		 * Gets the prototype string of this {@link Key}, uniquely identifying this {@link Key} among all other {@link Key}s of the {@link Intertwine}.
		 */
		String getPrototypeString();
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
	 * Obtains a key given the zero-based index of the method. (Useful only if binary compatibility is guaranteed or it has somehow been negotiated.)
	 */
	Key<T> keyByIndex( int id );

	/**
	 * Obtains a {@link Key} given a prototype string.
	 */
	Key<T> keyByPrototypeString( String prototypeString );

	/**
	 * Creates a new implementation of the target interface which delegates to the given instance of {@link AnyCall}.
	 */
	T newEntwiner( AnyCall<T> exitPoint );

	/**
	 * Creates a new implementation of {@link AnyCall} which delegates to the given instance of the target interface.
	 */
	AnyCall<T> newUntwiner( T exitPoint );

	/**
	 * Builds a prototype string from a method.
	 *
	 * @param method the method to get the prototype string of.
	 *
	 * @return the prototype string of the method.
	 */
	static String prototypeString( Method method )
	{
		Collection<Class<?>> parameterTypes = List.of( method.getParameterTypes() );
		return parameterTypes.stream().map( Class::getCanonicalName ).collect( Collectors.joining( ",", method.getName() + "(", ")" ) );
	}
}
