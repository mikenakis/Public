package mikenakis.immutability.internal.mykit;

import java.util.Objects;

public interface EqualityComparator<T>
{
	static <T> EqualityComparator<T> byReference()
	{
		return ( a, b ) -> a == b;
	}

	static <T> EqualityComparator<T> byValue()
	{
		return ( a, b ) -> Objects.equals( a, b );
	}

	boolean equals( T a, T b );
}
