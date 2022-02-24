package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableArrayMap;
import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableList;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;

/**
 * A caching map backed by an array.
 *
 * @author michael.gr
 */
final class CachingArrayMap<K, V> extends AbstractMutableMap<K,V> implements MutableArrayMap.Defaults<K,V>
{
	private final class MyBinding implements Binding<K,V>
	{
		final K key;
		V value;

		MyBinding( K key, V value )
		{
			assert key != null;
			this.key = key;
			this.value = value;
		}

		@Override public K getKey()
		{
			return key;
		}

		@Override public V getValue()
		{
			return value;
		}
	}

	private final int capacity;
	private final MutableList<MyBinding> bindings = mutableCollections.newArrayList();
	private final MutableArraySet<K> keys;
	private final MutableList<V> values;

	CachingArrayMap( MutableCollections mutableCollections, int capacity, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.capacity = capacity;
		keys = new MutableArrayMapKeysList<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableArrayMapValuesList<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public boolean isFrozen()
	{
		return false;
	}

	@Override public MutableList<Binding<K,V>> mutableEntries()
	{
		return MutableList.downCast( bindings );
	}

	@Override public MutableArraySet<K> mutableKeys()
	{
		return keys;
	}

	@Override public MutableList<V> mutableValues()
	{
		return values;
	}

	@Override public int size()
	{
		return bindings.size();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert isReadableAssertion();
		int index = find( key );
		if( index == -1 )
			return Optional.empty();
		Binding<K,V> binding = bindings.get( index );
		if( index + 1 < bindings.size() )
			bindings.swapAt( index, index + 1 );
		return Optional.of( binding );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		int index = find( key );
		if( index != -1 )
			return Optional.of( bindings.get( index ).value );
		MyBinding newBinding = new MyBinding( key, value );
		int newIndex = bindings.size() / 2;
		bindings.insertAt( newIndex, newBinding );
		trim();
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		int index = find( key );
		if( index == -1 )
		{
			MyBinding newBinding = new MyBinding( key, value );
			int newIndex = bindings.size() / 2;
			bindings.insertAt( newIndex, newBinding );
		}
		else
		{
			MyBinding binding = bindings.get( index );
			binding.value = value;
			if( index + 1 < bindings.size() )
				bindings.swapAt( index, index + 1 );
		}
		trim();
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert isWritableAssertion();
		int index = find( key );
		if( index == -1 )
			return false;
		bindings.removeAt( index );
		return true;
	}

	@Override public boolean clear()
	{
		assert isWritableAssertion();
		return bindings.clear();
	}

	private void trim()
	{
		while( bindings.size() > capacity )
		{
			Binding<K,V> binding = bindings.extractFirstElement();
			onElementRemoved( binding.getKey(), binding.getValue() );
		}
	}

	@SuppressWarnings( { "EmptyMethod", "unused" } ) private void onElementRemoved( K key, V value ) //TODO trigger event
	{
		/* nothing to do */
	}

	private int find( K key )
	{
		for( int i = 0; i < bindings.size(); i++ )
		{
			Binding<K,V> binding = bindings.get( i );
			if( binding.getKey().equals( key ) )
				return i;
		}
		return -1;
	}
}
