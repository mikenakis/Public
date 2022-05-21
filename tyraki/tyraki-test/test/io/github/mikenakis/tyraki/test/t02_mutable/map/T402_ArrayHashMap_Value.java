package io.github.mikenakis.tyraki.test.t02_mutable.map;

import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T402_ArrayHashMap_Value extends MutableMapTest<ValueSemanticsClass>
{
	private int number = 5;

	public T402_ArrayHashMap_Value()
	{
	}

	@Override protected MutableMap<ValueSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newArrayHashMap( 4, 0.75f );
	}

	@Override protected ValueSemanticsClass newKey()
	{
		return new ValueSemanticsClass( number++ );
	}

	@Override protected boolean isOrdered()
	{
		return true;
	}
}
