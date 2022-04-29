package mikenakis.allocation;

import mikenakis.kit.Kit;

import java.util.HashSet;
import java.util.Set;

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

	private static final Set<String> allocationKeys = Kit.areAssertionsEnabled() ? new HashSet<>() : null;

	private static boolean uniqueAssertion( String name )
	{
		Kit.sync.synchronize( allocationKeys, () -> //
			Kit.collection.add( allocationKeys, name ) );
		return true;
	}
}
