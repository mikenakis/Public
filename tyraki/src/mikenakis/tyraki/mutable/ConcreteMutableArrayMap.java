package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableArrayMap;
import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;

import java.util.Optional;

/**
 * {@link MutableMap} implemented using an array of {@link Binding}.
 *
 * @author michael.gr
 */
final class ConcreteMutableArrayMap<K, V> extends AbstractMutableMap<K,V> implements MutableArrayMap.Defaults<K,V>
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

	private final class MyEntries extends AbstractMutableEntries implements MutableList.Decorator<Binding<K,V>>
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public MutableList<Binding<K,V>> getDecoratedMutableList()
		{
			return MutableList.downCast( bindingsList );
		}
	}

	private final MyEntries entries;
	private final MutableList<MyBinding> bindingsList = mutableCollections.newArrayList();
	private final MutableArraySet<K> keys;
	private final MutableList<V> values;

	ConcreteMutableArrayMap( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		entries = new MyEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MutableArrayMapKeysList<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableArrayMapValuesList<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public MutableList<Binding<K,V>> mutableEntries()
	{
		return entries;
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
		assert canReadAssertion();
		return bindingsList.size();
	}

	private int find( K key )
	{
		assert key != null;
		EqualityComparator<? super K> keyEqualityComparator = keys().getEqualityComparator();
		for( int i = 0; i < bindingsList.size(); i++ )
		{
			Binding<K,V> binding = bindingsList.get( i );
			if( keyEqualityComparator.equals( binding.getKey(), key ) )
				return i;
		}
		return -1;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert canReadAssertion();
		int index = find( key );
		if( index == -1 )
			return Optional.empty();
		return Optional.of( bindingsList.get( index ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert canMutateAssertion();
		Optional<Binding<K,V>> existing = tryGetBindingByKey( key );
		if( existing.isPresent() )
			return Optional.of( existing.get().getValue() );
		MyBinding myBinding = new MyBinding( key, value );
		bindingsList.add( myBinding );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert canMutateAssertion();
		int index = find( key );
		if( index == -1 )
			return false;
		MyBinding myBinding = bindingsList.get( index );
		myBinding.value = value;
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert canMutateAssertion();
		int index = find( key );
		if( index == -1 )
			return false;
		bindingsList.removeAt( index );
		return true;
	}

	@Override public boolean clear()
	{
		assert canMutateAssertion();
		return bindingsList.clear();
	}
}
