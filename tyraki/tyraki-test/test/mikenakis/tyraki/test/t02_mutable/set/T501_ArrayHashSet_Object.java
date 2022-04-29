package mikenakis.tyraki.test.t02_mutable.set;

import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T501_ArrayHashSet_Object extends MutableSetTest<ObjectSemanticsClass>
{
	public T501_ArrayHashSet_Object()
	{
	}

	@Override protected MutableCollection<ObjectSemanticsClass> newCollection()
	{
		return SingleThreadedMutableCollections.instance().newIdentityArrayHashSet();
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
