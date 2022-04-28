package mikenakis.tyraki.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T01_Iterator_On_Java_ArrayList extends IteratorTest
{
	public T01_Iterator_On_Java_ArrayList()
	{
	}

	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	@Override protected final <T> Iterable<T> onCreateIterable( T... elements )
	{
		List<T> list = Arrays.asList( elements );
		return new ArrayList<>( list );
	}
}
