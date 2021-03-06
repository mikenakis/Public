package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.coherence.implementation.ThreadLocalCoherence;

/**
 * Single-threaded {@link MutableCollections}.
 *
 * @author michael.gr
 */
public final class SingleThreadedMutableCollections
{
	private static final ThreadLocal<MutableCollections> instance = ThreadLocal.withInitial( () -> //
		MutableCollections.of( ThreadLocalCoherence.instance() ) );

	public static MutableCollections instance()
	{
		return instance.get();
	}
}
