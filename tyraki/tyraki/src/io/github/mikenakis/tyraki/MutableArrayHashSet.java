package io.github.mikenakis.tyraki;

/**
 * Mutable Array Hash Set.
 *
 * @author michael.gr
 */
public interface MutableArrayHashSet<E> extends MutableHashSet<E>, MutableArraySet<E>, UnmodifiableArrayHashSet<E>
{
	interface Defaults<E> extends MutableArrayHashSet<E>, MutableHashSet.Defaults<E>, MutableArraySet.Defaults<E>, UnmodifiableArrayHashSet.Defaults<E>
	{
	}
}
