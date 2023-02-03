package io.github.mikenakis.intertwine;

import java.lang.reflect.Method;
import java.util.List;

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
	List<MethodKey<T>> keys();

	/**
	 * Obtains a {@link MethodKey} given a {@link Method}.
	 */
	MethodKey<T> keyByMethod( Method method );

	/**
	 * Creates a new implementation of the target interface which delegates to the given instance of {@link Anycall}.
	 */
	T newEntwiner( Anycall<T> exitPoint );

	/**
	 * Creates a new implementation of {@link Anycall} which delegates to the given instance of the target interface.
	 */
	Anycall<T> newUntwiner( T exitPoint );
}
