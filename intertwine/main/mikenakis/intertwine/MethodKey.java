package mikenakis.intertwine;

import mikenakis.bytecode.model.descriptors.MethodPrototype;

/**
 * Identifies a method of an interface.
 *
 * @author michael.gr
 */
public interface MethodKey<T>
{
	/**
	 * Gets the {@link Intertwine} that created this {@link MethodKey}.
	 */
	Intertwine<T> getIntertwine();

	/**
	 * Gets the index of this {@link MethodKey}.
	 */
	int methodIndex();

	/**
	 * Gets the {@link MethodPrototype} of this {@link MethodKey}.
	 */
	MethodPrototype methodPrototype();
}
