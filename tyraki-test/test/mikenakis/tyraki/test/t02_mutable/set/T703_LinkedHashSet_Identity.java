package mikenakis.tyraki.test.t02_mutable.set;

import mikenakis.kit.IdentityEqualityComparator;
import mikenakis.tyraki.IdentityHasher;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.mutable.singlethreaded.SingleThreadedMutableCollections;
import mikenakis.tyraki.test.t02_mutable.ValueSemanticsClass;

/**
 * Test.
 *
 * @author michael.gr
 */
public final class T703_LinkedHashSet_Identity extends MutableSetTest<ValueSemanticsClass>
{
	public T703_LinkedHashSet_Identity()
	{
	}

	@Override protected MutableCollection<ValueSemanticsClass> newCollection()
	{
		return SingleThreadedMutableCollections.instance().newLinkedHashSet( 4, 0.75f, IdentityHasher.getInstance(), IdentityEqualityComparator.getInstance() );
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
