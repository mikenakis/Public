package mikenakis.kit;

import java.util.Comparator;

/**
 * The Default {@link Comparator} for all classes implementing {@link Comparable}.
 *
 * @param <E> the type of the comparable objects.
 *
 * @author michael.gr
 */
public final class DefaultComparator<E extends Comparable<E>> implements Comparator<E>
{
	private static final DefaultComparator<? extends Comparable<?>> INSTANCE = new DefaultComparator<>();

	/**
	 * Get an instance of DefaultComparator for any type of Comparable.
	 *
	 * @param <T> the type of Comparable of interest.
	 *
	 * @return an instance of DefaultComparator for comparing instances of the requested type.
	 */
	public static <T> Comparator<T> getInstance()
	{
		@SuppressWarnings( "unchecked" )
		Comparator<T> result = (Comparator<T>)INSTANCE;
		return result;
	}

	private DefaultComparator()
	{
	}

	@Override public int compare( E o1, E o2 )
	{
		if( o1 == o2 )
			return 0;
		if( o1 == null )
			return 1;
		if( o2 == null )
			return -1;
		return o1.compareTo( o2 );
	}

	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
