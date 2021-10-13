package mikenakis.tyraki.test;

import mikenakis.kit.functional.Procedure0;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.legacy.LegacyCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test.
 *
 * @author michael.gr
 */
public class T02_Enumerator_On_Java_ArrayList extends EnumeratorTest
{
	public T02_Enumerator_On_Java_ArrayList()
	{
	}

	private static class ListFactory<T> implements Factory<T>
	{
		private final List<T> list;

		ListFactory( List<T> list )
		{
			this.list = list;
		}

		@Override public MutableEnumerator<T> getResults()
		{
			return LegacyCollections.newEnumeratorOnJavaIterator( list.iterator(), Procedure0.noOp );
		}
	}

	@SafeVarargs
	@SuppressWarnings( "varargs" ) //for -Xlint
	@Override protected final <T> Factory<T> onCreateFactory( T... elements )
	{
		return new ListFactory<>( new ArrayList<>( Arrays.asList( elements ) ) );
	}
}
