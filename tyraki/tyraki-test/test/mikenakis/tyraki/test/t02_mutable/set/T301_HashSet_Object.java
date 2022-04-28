package mikenakis.tyraki.test.t02_mutable.set;

import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ObjectSemanticsClass;

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
		return SingleThreadedMutableCollections.instance().newHashSet( 4, 0.75f );
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
