package io.github.mikenakis.tyraki.test.t02_mutable.map;

import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.IdentityEqualityComparator;
import io.github.mikenakis.tyraki.IdentityHasher;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.mutable.SingleThreadedMutableCollections;
import io.github.mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T603_LinkedHashMap_Identity extends MutableMapTest<ValueSemanticsClass>
{
	public T603_LinkedHashMap_Identity()
	{
	}

	@Override protected MutableMap<ValueSemanticsClass,String> newMap()
	{
		return SingleThreadedMutableCollections.instance().newLinkedHashMap( IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance(), DefaultEqualityComparator.getInstance() );
	}

	@Override protected ValueSemanticsClass newKey()
	{
		return new ValueSemanticsClass( 55 ); //same value, but treated as object, so it should be ok.
	}

	@Override protected boolean isOrdered()
	{
		return false;
	}
}
