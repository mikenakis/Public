package io.github.mikenakis.tyraki.test.t02_mutable.list;

import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T103_ArrayList_Identity extends MutableListTest<ValueSemanticsClass>
{
	public T103_ArrayList_Identity()
	{
	}

	@Override protected MutableList<ValueSemanticsClass> newList()
	{
		return SingleThreadedMutableCollections.instance().newIdentityArrayList();
	}

	@Override protected ValueSemanticsClass newElement()
	{
		return new ValueSemanticsClass( 55 );
	}

	@Override protected boolean isOrdered()
	{
		return true;
	}
}
