package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

class MutableMapKeysCollection<K, V> extends AbstractMutableCollection<K>
{
	private final MutableMap<K,V> map;

	MutableMapKeysCollection( MutableCollections mutableCollections, MutableMap<K,V> map, EqualityComparator<? super K> keyEqualityComparator )
	{
		super( mutableCollections, keyEqualityComparator );
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
		if( binding.isEmpty() )
			return Optional.empty();
		return Optional.of( binding.get().getKey() );
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
		assert element != null;
		assert false : element; //cannot add a key, because I do not know what value to associate it with.
		// TODO what this means is that even a mutable map should have structurally immutable keys and values collections!  If you really want a mutable collection on map keys or values
		// then you should have to instantiate some custom collection, passing it a method to create a value given a key, or vice versa, and if such a function cannot be defined,
		// then to pass a function that asserts "false", assuming all responsibility in the event that the function gets invoked.
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
}
