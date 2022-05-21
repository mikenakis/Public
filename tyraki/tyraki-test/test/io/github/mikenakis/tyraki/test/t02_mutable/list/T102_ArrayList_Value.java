package io.github.mikenakis.tyraki.test.t02_mutable.list;

import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T102_ArrayList_Value extends MutableListTest<ValueSemanticsClass>
{
	private int number = 55;

	public T102_ArrayList_Value()
	{
	}

	@Override protected MutableList<ValueSemanticsClass> newList()
	{
		return SingleThreadedMutableCollections.instance().newArrayList();
	}

	@Override protected ValueSemanticsClass newElement()
	{
		return new ValueSemanticsClass( number++ );
	}

	@Override protected boolean isOrdered()
	{
		return true;
	}
}
