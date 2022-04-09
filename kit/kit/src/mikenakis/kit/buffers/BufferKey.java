package mikenakis.kit.buffers;

import mikenakis.kit.Kit;

import java.util.HashMap;
import java.util.Map;

/**
 * Buffer Key.
 *
 * @author michael.gr
 */
public final class BufferKey
{
	private static final Map<String,BufferKey> bufferKeys = new HashMap<>();

	private static boolean uniqueAssertion( BufferKey bufferKey )
	{
		Kit.sync.synchronize( bufferKeys, () -> //
			Kit.map.add( bufferKeys, bufferKey.name, bufferKey ) );
		return true;
	}

	private final String name;

	public BufferKey( String name )
	{
		assert !name.isEmpty();
		this.name = name;
		assert uniqueAssertion( this );
	}

	public boolean equals( BufferKey other )
	{
		return this == other;
	}

	@Override public boolean equals( Object other )
	{
		if( other instanceof BufferKey )
			return equals( (BufferKey)other );
		assert false;
		return false;
	}

	@Override public int hashCode()
	{
		return System.identityHashCode( this );
	}

	@Override public String toString()
	{
		return "\"" + name + "\"";
	}
}
