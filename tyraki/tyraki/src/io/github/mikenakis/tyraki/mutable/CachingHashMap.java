package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.DefaultEqualityComparator;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableHashMap;
import io.github.mikenakis.tyraki.MutableMap;
import io.github.mikenakis.tyraki.ObjectHasher;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * A caching map backed by a hash map.
 *
 * @author michael.gr
 */
final class CachingHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
{
	private class Item implements Binding<K,V>
	{
		public final int timestamp;
		public final K key;
		public final V value;

		Item( int timestamp, K key, V value )
		{
			assert key != null;
			this.timestamp = timestamp;
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

		@SuppressWarnings( "unchecked" ) @Deprecated @Override public boolean equals( Object o )
		{
			return o instanceof CachingHashMap<?, ?>.Item kin && equals( (CachingHashMap<K, V>.Item)kin );
		}

		public boolean equals( Item item )
		{
			return timestamp == item.timestamp &&
				keyEqualityComparator.equals( key, item.key ) &&
				valueEqualityComparator.equals( value, item.value );
		}

		@Override public int hashCode()
		{
			int keyHash = keyHasher.getHashCode( key );
			return Objects.hash( timestamp, keyHash, value );
		}
	}

	private final class MyEntries extends AbstractMutableEntries
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return keysToItems.entries().getModificationCount();
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			return keysToItems.mutableEntries().newMutableEnumerator().map( binding -> MapEntry.of( binding.getKey(), binding.getValue().value ) );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return keysToItems.entries().newUnmodifiableEnumerator().map( binding -> MapEntry.of( binding.getKey(), binding.getValue().value ) );
		}
	}

	private final Hasher<? super K> keyHasher;
	private final EqualityComparator<? super K> keyEqualityComparator;
	private final EqualityComparator<? super V> valueEqualityComparator;
	private final MyEntries entries;
	private final int capacity;
	private int timestampSeed;
	private final MutableMap<K,Item> keysToItems;
	private final MutableCollection<Item> items = mutableCollections.newTreeSet( ObjectHasher.INSTANCE, Comparator.comparingInt( a -> a.timestamp ) );
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;

	CachingHashMap( MutableCollections mutableCollections, int capacity, Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		this.keyEqualityComparator = keyEqualityComparator;
		this.valueEqualityComparator = valueEqualityComparator;
		keysToItems = mutableCollections.newHashMap( keyHasher, keyEqualityComparator, DefaultEqualityComparator.getInstance() );
		this.capacity = capacity;
		entries = new MyEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MutableMapKeysCollection<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableMapValuesCollection<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public MutableCollection<Binding<K,V>> mutableEntries()
	{
		return entries;
	}

	@Override public MutableCollection<K> mutableKeys()
	{
		return keys;
	}

	@Override public MutableCollection<V> mutableValues()
	{
		return values;
	}

	@Override public Hasher<? super K> getKeyHasher()
	{
		return keyHasher;
	}

	@Override public int size()
	{
		return keysToItems.size();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		Optional<Item> item = keysToItems.tryGet( key );
		if( item.isEmpty() )
			return Optional.empty();
		items.remove( item.get() );
		Item newItem = new Item( timestampSeed++, key, item.get().value );
		keysToItems.replaceValue( key, newItem );
		items.add( newItem );
		trim();
		return Kit.downCast( item );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Optional<Item> oldItem = keysToItems.tryGet( key );
		if( oldItem.isPresent() )
			return Optional.of( oldItem.get().getValue() );
		Item newItem = new Item( timestampSeed++, key, value );
		keysToItems.add( key, newItem );
		items.add( newItem );
		trim();
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Item newItem = new Item( timestampSeed++, key, value );
		Optional<Item> oldItem = keysToItems.tryGet( key );
		if( oldItem.isEmpty() )
			keysToItems.add( key, newItem );
		else
		{
			items.remove( oldItem.get() );
			keysToItems.replaceValue( key, newItem );
		}
		items.add( newItem );
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Optional<Item> value = keysToItems.tryGet( key );
		if( value.isEmpty() )
			return false;
		items.remove( value.get() );
		keysToItems.removeKey( key );
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( items.isEmpty() )
			return false;
		boolean ok1 = keysToItems.clear();
		assert ok1;
		boolean ok2 = items.clear();
		assert ok2;
		return false;
	}

	private void trim()
	{
		while( items.size() > capacity )
		{
			Item item = items.extractFirstElement();
			keysToItems.removeKey( item.key );
			onElementRemoved( item.key, item.value );
		}
	}

	@SuppressWarnings( { "EmptyMethod", "unused" } ) private void onElementRemoved( K key, V value ) //TODO trigger event
	{
		/* nothing to do */
	}
}
