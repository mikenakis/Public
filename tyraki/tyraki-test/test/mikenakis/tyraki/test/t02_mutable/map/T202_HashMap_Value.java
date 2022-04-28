package mikenakis.tyraki.test.t02_mutable.map;

import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T202_HashMap_Value extends MutableMapTest<ValueSemanticsClass>
{
	private int number = 5;

	public T202_HashMap_Value()
	{
	}

	@Override protected MutableMap<ValueSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newHashMap( 4, 0.75f );
	}

	@Override protected ValueSemanticsClass newKey()
	{
		return new ValueSemanticsClass( number++ );
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
