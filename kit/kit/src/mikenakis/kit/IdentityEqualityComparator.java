package mikenakis.kit;

/**
 * An {@link EqualityComparator} which compares objects by reference.
 *
 * @author michael.gr
 */
public final class IdentityEqualityComparator<T> implements EqualityComparator<T>
{
	private static final EqualityComparator<?> INSTANCE = new IdentityEqualityComparator<>();

	public static <T> EqualityComparator<T> getInstance()
	{
		@SuppressWarnings( "unchecked" )
		EqualityComparator<T> result = (EqualityComparator<T>)INSTANCE;
		return result;
	}

	private IdentityEqualityComparator()
	{
	}

	@Override public boolean equals( T a, T b )
	{
		return a == b;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
