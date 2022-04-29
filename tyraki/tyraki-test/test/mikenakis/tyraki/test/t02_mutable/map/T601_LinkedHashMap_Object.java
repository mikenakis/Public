package mikenakis.tyraki.test.t02_mutable.map;

import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T601_LinkedHashMap_Object extends MutableMapTest<ObjectSemanticsClass>
{
	public T601_LinkedHashMap_Object()
	{
	}

	@Override protected MutableMap<ObjectSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newIdentityLinkedHashMap();
	}

	@Override protected ObjectSemanticsClass newKey()
	{
		return new ObjectSemanticsClass();
	}

	@Override protected boolean isOrdered()
	{
		return true;
	}
}
