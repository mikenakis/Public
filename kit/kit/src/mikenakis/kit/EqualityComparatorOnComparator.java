package mikenakis.kit;

import java.util.Comparator;

/**
 * An {@link EqualityComparator} which delegates to a {@link Comparator}.
 *
 * @author michael.gr
 */
public final class EqualityComparatorOnComparator<T> implements EqualityComparator<T>
{
	private final Comparator<T> comparator;

	public EqualityComparatorOnComparator( Comparator<T> comparator )
	{
		assert comparator != null;
		this.comparator = comparator;
	}

	@Override public boolean equals( T a, T b )
	{
		if( a == b )
			return true;
		if( a == null || b == null )
			return false;
		return comparator.compare( a, b ) == 0;
	}

	@Override public String toString()
	{
		return getClass().getSimpleName() + " " + comparator.toString();
	}
}
