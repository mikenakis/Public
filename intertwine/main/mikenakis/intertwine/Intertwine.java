package mikenakis.intertwine;

import mikenakis.bytecode.model.descriptors.MethodPrototype;

import java.util.Collection;

/**
 * Creates Entwiners and Untwiners for a given interface; provides mapping between method {@link MethodKey}s indexes, and prototype strings.
 *
 * @param <T> the type of the interface.
 *
 * @author michael.gr
 */
public interface Intertwine<T>
{
	/**
	 * Gets the type of the interface.
	 */
	Class<? super T> interfaceType();

	/**
	 * Gets all the keys of the interface.
	 */
	Collection<MethodKey<T>> keys();

	/**
	 * Obtains a key given the zero-based index of the method. (Useful only if binary compatibility is guaranteed or it has somehow been negotiated.)
	 */
	MethodKey<T> keyByIndex( int id );

	/**
	 * Obtains a {@link MethodKey} given a {@link MethodPrototype}.
	 */
	MethodKey<T> keyByMethodPrototype( MethodPrototype methodPrototype );

	/**
	 * Creates a new implementation of the target interface which delegates to the given instance of {@link Anycall}.
	 */
	T newEntwiner( Anycall<T> exitPoint );

	/**
	 * Creates a new implementation of {@link Anycall} which delegates to the given instance of the target interface.
	 */
	Anycall<T> newUntwiner( T exitPoint );
}
