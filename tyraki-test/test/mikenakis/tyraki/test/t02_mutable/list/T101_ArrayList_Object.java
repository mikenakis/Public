package mikenakis.tyraki.test.t02_mutable.list;

import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.mutable.singlethreaded.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T101_ArrayList_Object extends MutableListTest<ObjectSemanticsClass>
{
	public T101_ArrayList_Object()
	{
	}

	@Override protected MutableList<ObjectSemanticsClass> newList()
	{
		return SingleThreadedMutableCollections.instance().newArrayList();
	}

	@Override protected ObjectSemanticsClass newElement()
	{
		return new ObjectSemanticsClass();
	}

	@Override protected boolean isOrdered()
	{
		return true;
	}
}
