package mikenakis.tyraki.test.t02_mutable.map;

import mikenakis.kit.IdentityEqualityComparator;
import mikenakis.tyraki.IdentityHasher;
import mikenakis.tyraki.MutableMap;
import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.tyraki.mutable.singlethreaded.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T203_HashMap_Identity extends MutableMapTest<ValueSemanticsClass>
{
	public T203_HashMap_Identity()
	{
	}

	@Override protected MutableMap<ValueSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newHashMap( 4, 0.75f, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(),
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
