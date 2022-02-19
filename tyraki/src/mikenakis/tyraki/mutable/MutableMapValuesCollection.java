package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableMap;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

class MutableMapValuesCollection<K, V> extends AbstractMutableCollection<V>
{
	private final MutableMap<K,V> map;

	MutableMapValuesCollection( MutableCollections mutableCollections, MutableMap<K,V> map, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections, valueEqualityComparator );
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

	@Override public Optional<V> tryGet( V element )
	{
		Optional<Binding<K,V>> binding = map.tryGetBindingsByValue( element ).tryFetchFirstElement();
		if( binding.isEmpty() )
			return Optional.empty();
		return Optional.of( binding.get().getValue() );
	}

	@Override public int getModificationCount()
	{
		return map.entries().getModificationCount();
	}

	@Override public MutableEnumerator<V> newMutableEnumerator()
	{
		return map.mutableEntries().newMutableEnumerator().map( kvBinding -> kvBinding.getValue() );
	}

	@Override public boolean tryReplace( V oldElement, V newElement )
	{
		//TODO: if there was a MutableEnumerator.setCurrent() function, we could replace this loop with one which enumerates values instead of entries.
		for( Binding<K,V> binding : map.mutableEntries() )
		{
			if( binding.getValue() == oldElement )
			{
				map.replaceValue( binding.getKey(), newElement );
				return true;
			}
		}
		return false;
	}

	@Override public Optional<V> tryAdd( V element )
	{
		assert false; //this is meaningless, because we do not know what key to bind this value to.
		return Optional.empty();
	}

	//TODO: this method could accept a boolean specifying whether we want all values removed, or just the first matching value removed.
	@Override public boolean tryRemove( V element )
	{
		for( MutableEnumerator<V> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
		{
			if( enumerator.getCurrent() == element )
			{
				enumerator.deleteCurrent();
				return true;
			}
		}
		return false;
	}

	@Override public boolean clear()
	{
		return map.clear();
	}
}
