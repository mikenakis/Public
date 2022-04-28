package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.EqualityComparatorOnComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.legacy.LegacyCollections;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Tree Map.
 *
 * @author michael.gr
 */
final class ConcreteMutableTreeMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
{
	private final Function1<Binding<K,V>,Entry<Item,V>> converter = item -> MapEntry.of( item.getKey().key, item.getValue() );

	private final class MyEntries extends AbstractMutableEntries
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return modificationCount;
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			assert mustBeReadableAssertion();
			Iterator<Entry<Item,V>> iterator = javaMap.entrySet().iterator();
			MutableEnumerator<Entry<Item,V>> modifiableEnumerator = LegacyCollections.newEnumeratorOnJavaIterator( iterator, () -> modificationCount++ );
			return modifiableEnumerator.map( converter );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			assert mustBeReadableAssertion();
			return newMutableEnumerator();
		}
	}

	private final SortedMap<Item,V> javaMap = new TreeMap<>();
	private int modificationCount = 0;
	final Hasher<? super K> keyHasher;
	final Comparator<? super K> keyComparator;
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;

	ConcreteMutableTreeMap( MutableCollections mutableCollections, Hasher<? super K> keyHasher, Comparator<? super K> keyComparator,
		EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		this.keyComparator = keyComparator;
		EqualityComparator<? super K> keyEqualityComparator = new EqualityComparatorOnComparator<>( keyComparator );
		entries = new MyEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MutableMapKeysCollection<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableMapValuesCollection<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public MutableCollection<Binding<K,V>> mutableEntries()
	{
		assert mustBeReadableAssertion();
		return entries;
	}

	@Override public MutableCollection<K> mutableKeys()
	{
		assert mustBeReadableAssertion();
		return keys;
	}

	@Override public MutableCollection<V> mutableValues()
	{
		assert mustBeReadableAssertion();
		return values;
	}

	@Override public int size()
	{
		assert mustBeReadableAssertion();
		return javaMap.size();
	}

	@Override public Hasher<? super K> getKeyHasher()
	{
		assert mustBeReadableAssertion();
		return keyHasher;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		Item item = new Item( key );
		if( !javaMap.containsKey( item ) )
			return Optional.empty();
		return Optional.of( item );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Item item = new Item( key );
		V existing = javaMap.get( item );
		if( existing != null )
			return Optional.of( existing );
		Kit.map.add( javaMap, item, value );
		modificationCount++;
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Item item = new Item( key );
		if( !Kit.map.tryReplace( javaMap, item, value ) )
			return false;
		modificationCount++;
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Item item = new Item( key );
		if( !javaMap.containsKey( item ) )
			return true;
		Kit.map.remove( javaMap, item );
		modificationCount++;
		return false;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( javaMap.isEmpty() )
			return false;
		javaMap.clear();
		modificationCount++;
		return true;
	}

	private class Item implements Binding<K,V>, Comparable<Item>
	{
		final K key;

		Item( K key )
		{
			this.key = key;
		}

		public boolean equals( Item other )
		{
			return keyComparator.compare( key, other.key ) == 0;
		}

		@Override public boolean equals( Object o )
		{
			if( o instanceof ConcreteMutableTreeMap<?,?>.Item )
			{
				@SuppressWarnings( "unchecked" )
				Item otherItem = (Item)o;
				return equals( otherItem );
			}
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return keyHasher.getHashCode( key );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return String.valueOf( key );
		}

		@Override public int compareTo( Item o )
		{
			assert o != null;
			return keyComparator.compare( key, o.key );
		}

		@Override public K getKey()
		{
			return key;
		}

		@Override public V getValue()
		{
			return Kit.map.get( javaMap, this );
		}
	}
}
