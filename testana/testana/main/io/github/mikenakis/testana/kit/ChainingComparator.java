package io.github.mikenakis.testana.kit;

import java.util.Comparator;

public class ChainingComparator<T> implements Comparator<T>
{
	private final Comparator<? super T> comparator1;
	private final Comparator<? super T> comparator2;

	public ChainingComparator( Comparator<? super T> comparator1, Comparator<? super T> comparator2 )
	{
		this.comparator1 = comparator1;
		this.comparator2 = comparator2;
	}

	@Override public int compare( T o1, T o2 )
	{
		int result = comparator1.compare( o1, o2 );
		if( result != 0 )
			return result;
		return comparator2.compare( o1, o2 );
	}
}
