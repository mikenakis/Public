package io.github.mikenakis.tyraki.test.t02_mutable.set;

import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T301_HashSet_Object extends MutableSetTest<ObjectSemanticsClass>
{
	public T301_HashSet_Object()
	{
	}

	@Override protected MutableCollection<ObjectSemanticsClass> newCollection()
	{
		return SingleThreadedMutableCollections.instance().newIdentityHashSet();
	}

	@Override protected ObjectSemanticsClass newElement()
	{
		return new ObjectSemanticsClass();
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
