package mikenakis.kit;

/**
 * Implements {@link EqualityComparator} by invoking {@link Object#equals(Object)}.
 *
 * @author michael.gr
 */
public final class DefaultEqualityComparator<T> implements EqualityComparator<T>
{
	private static final EqualityComparator<?> INSTANCE = new DefaultEqualityComparator<>();

	public static <T> EqualityComparator<T> getInstance()
	{
		@SuppressWarnings( "unchecked" )
		EqualityComparator<T> result = (EqualityComparator<T>)INSTANCE;
		return result;
	}

	private DefaultEqualityComparator()
	{
	}

	@Override public boolean equals( T a, T b )
	{
		if( a == b )
			return true;
		if( a == null )
			return false;
		if( b == null )
			return false;
		return a.equals( b );
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
