package mikenakis.tyraki.mutable;

import mikenakis.tyraki.DebugView;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.UnmodifiableCollection;

/**
 * Abstract {@link UnmodifiableCollection}.
 *
 * @author michael.gr
 */
abstract class AbstractMutableEnumerable<E> extends MutableCollectionsSubject implements MutableEnumerable.Defaults<E>
{
	@SuppressWarnings( { "unused", "FieldNamingConvention" } )
	private final DebugView _debugView = DebugView.create( () -> this );

	protected AbstractMutableEnumerable( MutableCollections mutableCollections )
	{
		super( mutableCollections );
	}

	@SuppressWarnings( "SameReturnValue" )
	final boolean canReadAssertion()
	{
		assert isFrozen() || getMutableCollections().assertCoherence();
		return true;
	}

	@SuppressWarnings( "SameReturnValue" )
	@Override public final boolean canWriteAssertion()
	{
		assert !isFrozen() && getMutableCollections().assertCoherence();
		return true;
	}

	@Override public final int hashCode()
	{
		return calculateHashCode();
	}

	@Override public final String toString()
	{
		return countElements() + " elements";
	}
}
