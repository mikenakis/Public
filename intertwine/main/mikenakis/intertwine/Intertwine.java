package mikenakis.intertwine;

import mikenakis.bytecode.model.descriptors.MethodPrototype;

import java.util.Collection;

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
		 * Gets the {@link MethodPrototype} of this {@link Key}, uniquely identifying this {@link Key} among all other {@link Key}s of the {@link Intertwine}.
		 */
		MethodPrototype getMethodPrototype();
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
	 * Obtains a {@link Key} given a {@link MethodPrototype}.
	 */
	Key<T> keyByMethodPrototype( MethodPrototype methodPrototype );

	/**
	 * Creates a new implementation of the target interface which delegates to the given instance of {@link AnyCall}.
	 */
	T newEntwiner( AnyCall<T> exitPoint );

	/**
	 * Creates a new implementation of {@link AnyCall} which delegates to the given instance of the target interface.
	 */
	AnyCall<T> newUntwiner( T exitPoint );
}
