package mikenakis.tyraki.test.t02_mutable.set;

import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T303_HashSet_Identity extends MutableSetTest<ValueSemanticsClass>
{
	public T303_HashSet_Identity()
	{
	}

	@Override protected MutableCollection<ValueSemanticsClass> newCollection()
	{
		return SingleThreadedMutableCollections.instance().newIdentityHashSet();
	}

	@Override protected ValueSemanticsClass newElement()
	{
		return new ValueSemanticsClass( 55 ); //always the same number, but this is for identity, so it should be okay.
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
