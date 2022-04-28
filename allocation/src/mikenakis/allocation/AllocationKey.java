package mikenakis.allocation;

import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;

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
		this.name = name;
		assert uniqueAssertion( this );
	}

	public boolean equals( AllocationKey other ) { return this == other; }
	@Override public boolean equals( Object other ) { return other instanceof AllocationKey kin && equals( kin ); }
	@Override public int hashCode() { return System.identityHashCode( this ); }
	@Override public String toString() { return "\"" + name + "\""; }

	private static final Map<String,AllocationKey> allocationKeys = Kit.areAssertionsEnabled() ? new HashMap<>() : null;

	private static boolean uniqueAssertion( AllocationKey allocationKey )
	{
		Kit.sync.synchronize( allocationKeys, () -> //
			Kit.map.add( allocationKeys, allocationKey.name, allocationKey ) );
		return true;
	}
}
