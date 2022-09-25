package io.github.mikenakis.allocation;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.Unit;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link Allocation} Key.
 *
 * @author michael.gr
 */
public final class AllocationKey
{
	private final String name;

	public AllocationKey( String name )
	{
		assert !name.isEmpty();
		assert uniqueAssertion( name );
		this.name = name;
	}

	@Override public String toString() { return "\"" + name + "\""; }

	private static final Lock lock = new ReentrantLock();
	private static final Set<String> allocationKeys = Kit.areAssertionsEnabled() ? new HashSet<>() : null;

	private static boolean uniqueAssertion( String name )
	{
		Kit.sync.lock( lock, () -> //
		{
			Kit.collection.add( allocationKeys, name ); //will fail if the key already exists.
			return Unit.instance;
		} );
		return true;
	}
}
