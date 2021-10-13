package mikenakis.tyraki.test.t02_mutable.set;

import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.singlethreaded.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T702_LinkedHashSet_Value extends MutableSetTest<ValueSemanticsClass>
{
	private int number = 500;

	public T702_LinkedHashSet_Value()
	{
	}

	@Override protected MutableCollection<ValueSemanticsClass> newCollection()
	{
		return SingleThreadedMutableCollections.instance().newLinkedHashSet( 4, 0.75f );
	}

	@Override protected ValueSemanticsClass newElement()
	{
		return new ValueSemanticsClass( number++ );
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
