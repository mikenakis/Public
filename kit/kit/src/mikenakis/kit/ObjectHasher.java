package mikenakis.kit;

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
		//TODO assert MachineDomain.instance().getRuntimeImmutabilityChecker().isImmutableObject( item );
		return item.hashCode();
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
