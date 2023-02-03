package io.github.mikenakis.intertwine;

import java.lang.reflect.Method;

/**
 * Represents a method of an interface.
 *
 * @author michael.gr
 */
public interface MethodKey<T>
{
	/**
	 * Gets the {@link Intertwine} that created this {@link MethodKey}.
	 */
	Intertwine<T> intertwine();

	/**
	 * Gets the index of the method represented by this {@link MethodKey}.
	 */
	int methodIndex();

	/**
	 * Gets the {@link Method} represented by this {@link MethodKey}.
	 */
	Method method();
}
