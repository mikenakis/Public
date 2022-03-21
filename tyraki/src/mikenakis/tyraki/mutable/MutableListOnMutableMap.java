package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * A {@link MutableList} on a {@link MutableMap} whose keys are of type {@link Integer}.
 *
 * @author michael.gr
 */
final class MutableListOnMutableMap<T> extends AbstractMutableList<T>
{
	private final MutableMap<Integer,T> map;

	MutableListOnMutableMap( MutableCollections mutableCollections, MutableMap<Integer,T> map )
	{
		super( mutableCollections, map.values().getEqualityComparator() );
		this.map = map;
	}

	@Override public int size()
	{
		assert canReadAssertion();
		return map.size();
	}

	@Override public boolean clear()
	{
		assert canMutateAssertion();
		return map.clear();
	}

	@Override public void replaceAt( int index, T element )
	{
		assert canMutateAssertion();
		map.replaceValue( index, element );
	}

	@Override public void insertAt( int index, T element )
	{
		assert canMutateAssertion();
		assert index == size(); //TODO: we can actually insert values, but it is a bit of work. Implement if there is a need.
		map.add( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert canMutateAssertion();
		map.removeKey( index );
	}

	@Override public T get( int index )
	{
		assert canReadAssertion();
		return map.get( index );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		assert canMutateAssertion();
		return map.mutableEntries().newMutableEnumerator().map( integerTBinding -> integerTBinding.getValue() );
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		assert canReadAssertion();
		return map.entries().newUnmodifiableEnumerator().map( integerTBinding -> integerTBinding.getValue() );
	}

	@Override public int getModificationCount()
	{
		assert canReadAssertion();
		return map.mutableEntries().getModificationCount();
	}
}
