package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableArrayMap;
import io.github.mikenakis.tyraki.MutableArraySet;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

final class MutableArrayMapKeysList<K, V> extends AbstractMutableList<K> implements MutableArraySet.Defaults<K>
{
	private final MutableArrayMap<K,V> map;

	MutableArrayMapKeysList( MutableCollections domain, MutableArrayMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator )
	{
		super( domain, keyEqualityComparator );
		this.map = map;
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<K> tryGet( K element )
	{
		assert element != null;
		Optional<Binding<K,V>> binding = map.tryGetBindingByKey( element );
		return binding.map( b -> b.getKey() );
	}

	@Override public int getModificationCount()
	{
		return map.entries().getModificationCount();
	}

	@Override public MutableEnumerator<K> newMutableEnumerator()
	{
		assert mustBeWritableAssertion();
		return map.mutableEntries().newMutableEnumerator().map( kvBinding -> kvBinding.getKey() );
	}

	@Override public UnmodifiableEnumerator<K> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return map.entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getKey() );
	}

	@Override public boolean tryReplace( K oldElement, K newElement )
	{
		assert oldElement != null;
		assert newElement != null;
		if( !map.containsKey( oldElement ) )
			return false;
		if( map.containsKey( newElement ) )
			return false;
		V oldValue = map.get( oldElement );
		map.removeKey( oldElement );
		map.add( newElement, oldValue );
		return true;
	}

	@Override public Optional<K> tryAdd( K element )
	{
		assert false : element; //cannot add a key, because I do not know what value to associate it with.
		return Optional.empty();
	}

	@Override public boolean tryRemove( K element )
	{
		assert element != null;
		return map.tryRemoveKey( element );
	}

	@Override public boolean clear()
	{
		return map.clear();
	}

	@Override public void replaceAt( int index, K element )
	{
		assert element != null;
		Binding<K,V> oldEntry = map.entries().get( index );
		Binding<K,V> newEntry = MapEntry.of( element, oldEntry.getValue() );
		map.mutableEntries().replaceAt( index, newEntry );
	}

	@Override public void insertAt( int index, K element )
	{
		assert element != null;
		assert false; //not implemented.  The list should be structurally immutable.
//		assert element != null : new IllegalArgumentException();
//		Binding<K,V> newEntry = MapEntry.of( element, null );
//		map.getMutableEntries().insertAt( index, newEntry );
	}

	@Override public void removeAt( int index )
	{
		map.mutableEntries().removeAt( index );
	}

	@Override public K get( int index )
	{
		return map.entries().get( index ).getKey();
	}
}
