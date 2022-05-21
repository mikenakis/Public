package io.github.mikenakis.tyraki.test.t02_mutable.map;

import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

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
