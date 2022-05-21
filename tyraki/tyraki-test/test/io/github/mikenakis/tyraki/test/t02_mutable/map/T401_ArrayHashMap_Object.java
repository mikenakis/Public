package io.github.mikenakis.tyraki.test.t02_mutable.map;

import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T401_ArrayHashMap_Object extends MutableMapTest<ObjectSemanticsClass>
{
	public T401_ArrayHashMap_Object()
	{
	}

	@Override protected MutableMap<ObjectSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newIdentityArrayHashMap();
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
