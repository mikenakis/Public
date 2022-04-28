package mikenakis.tyraki;

import mikenakis.immutability.object.ObjectImmutabilityAssessor;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;

/**
 * Implements {@link Hasher} by delegating to {@link Object#hashCode()}.
 *
 * @author michael.gr
 */
public final class ObjectHasher implements Hasher<Object>
{
	public static final Hasher<Object> INSTANCE = new ObjectHasher();

	private ObjectHasher()
	{
	}

	@Override public int getHashCode( Object item )
	{
		if( Kit.get( false ) ) //FIXME XXX TODO enable this!
			assert ObjectImmutabilityAssessor.instance.mustBeImmutableAssertion( item );
		return item.hashCode();
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
