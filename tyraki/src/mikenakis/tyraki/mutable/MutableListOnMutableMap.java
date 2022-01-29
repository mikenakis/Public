package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;

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

	@Override public boolean isFrozen()
	{
		return map.isFrozen();
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public boolean clear()
	{
		return map.clear();
	}

	@Override public void replaceAt( int index, T element )
	{
		map.replaceValue( index, element );
	}

	@Override public void insertAt( int index, T element )
	{
		assert index == size(); //TODO: we can actually insert values, but it is a bit of work. Implement if there is a need.
		map.add( index, element );
	}

	@Override public void removeAt( int index )
	{
		map.removeKey( index );
	}

	@Override public T get( int index )
	{
		return map.get( index );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		MutableEnumerator<Binding<Integer,T>> enumerator = map.mutableEntries().newMutableEnumerator();
		return enumerator.map( integerTBinding -> integerTBinding.getValue() );
	}

	@Override public int getModificationCount()
	{
		return map.mutableEntries().getModificationCount();
	}
}
