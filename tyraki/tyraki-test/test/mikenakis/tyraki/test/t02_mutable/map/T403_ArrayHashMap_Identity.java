package mikenakis.tyraki.test.t02_mutable.map;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.IdentityEqualityComparator;
import mikenakis.tyraki.IdentityHasher;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T403_ArrayHashMap_Identity extends MutableMapTest<ValueSemanticsClass>
{
	public T403_ArrayHashMap_Identity()
	{
	}

	@Override protected MutableMap<ValueSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newArrayHashMap( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(),
			DefaultEqualityComparator.getInstance() );
	}

	@Override protected ValueSemanticsClass newKey()
	{
		return new ValueSemanticsClass( 55 ); //same value, but treated as object, so it should be ok.
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
