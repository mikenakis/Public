package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableList;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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
		assert mustBeReadableAssertion();
		return map.size();
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		return map.clear();
	}

	@Override public void replaceAt( int index, T element )
	{
		assert mustBeWritableAssertion();
		map.replaceValue( index, element );
	}

	@Override public void insertAt( int index, T element )
	{
		assert mustBeWritableAssertion();
		assert index == size(); //TODO: we can actually insert values, but it is a bit of work. Implement if there is a need.
		map.add( index, element );
	}

	@Override public void removeAt( int index )
	{
		assert mustBeWritableAssertion();
		map.removeKey( index );
	}

	@Override public T get( int index )
	{
		assert mustBeReadableAssertion();
		return map.get( index );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		assert mustBeWritableAssertion();
		return map.mutableEntries().newMutableEnumerator().map( integerTBinding -> integerTBinding.getValue() );
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return map.entries().newUnmodifiableEnumerator().map( integerTBinding -> integerTBinding.getValue() );
	}

	@Override public int getModificationCount()
	{
		assert mustBeReadableAssertion();
		return map.mutableEntries().getModificationCount();
	}
}
