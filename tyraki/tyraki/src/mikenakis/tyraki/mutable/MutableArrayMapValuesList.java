package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableArrayMap;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

final class MutableArrayMapValuesList<K, V> extends AbstractMutableList<V>
{
	private final MutableArrayMap<K,V> map;

	MutableArrayMapValuesList( MutableCollections domain, MutableArrayMap<K,V> map, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( domain, valueEqualityComparator );
		this.map = map;
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<V> tryGet( V element )
	{
		Optional<Binding<K,V>> binding = map.tryGetBindingsByValue( element ).tryFetchFirstElement();
		return binding.map( b -> b.getValue() );
	}

	@Override public int getModificationCount()
	{
		return map.entries().getModificationCount();
	}

	@Override public MutableEnumerator<V> newMutableEnumerator()
	{
		assert mustBeWritableAssertion();
		return map.mutableEntries().newMutableEnumerator().map( kvBinding -> kvBinding.getValue() );
	}

	@Override public UnmodifiableEnumerator<V> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return map.entries().newUnmodifiableEnumerator().map( kvBinding -> kvBinding.getValue() );
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
			if( enumerator.current() == element )
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

	@Override public void replaceAt( int index, V element )
	{
		Binding<K,V> oldEntry = map.mutableEntries().get( index );
		Binding<K,V> newEntry = MapEntry.of( oldEntry.getKey(), element );
		map.mutableEntries().replaceAt( index, newEntry );
	}

	@Override public void insertAt( int index, V element )
	{
		assert false; //cannot be implemented
	}

	@Override public void removeAt( int index )
	{
		assert false; //not implemented
	}

	@Override public V get( int index )
	{
		return map.mutableEntries().get( index ).getValue();
	}
}
