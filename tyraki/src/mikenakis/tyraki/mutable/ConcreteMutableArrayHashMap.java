package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.tyraki.AbstractEnumerator;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableArrayHashMap;
import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.MutableMap;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * Array Hash Map.
 *
 * @author michael.gr
 */
final class ConcreteMutableArrayHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableArrayHashMap.Defaults<K,V>
{
	private final class MyEntries extends AbstractMutableEntries implements MutableList.Defaults<Binding<K,V>>
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public void replaceAt( int index, Binding<K,V> element )
		{
			K key = element.getKey();
			map.add( key, element.getValue() ); //ensures the key does not already exist
			keyList.replaceAt( index, key );
		}

		@Override public void insertAt( int index, Binding<K,V> element )
		{
			K key = element.getKey();
			map.add( key, element.getValue() );
			keyList.insertAt( index, key );
		}

		@Override public void removeAt( int index )
		{
			K key = keyList.getAndRemove( index );
			map.removeKey( key );
		}

		@Override public Binding<K,V> get( int index )
		{
			K key = keyList.get( index );
			V value = map.get( key );
			return MapEntry.of( key, value );
		}

		@Override public int getModificationCount()
		{
			return map.entries().getModificationCount() + keyList.getModificationCount();
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			MutableEnumerator<K> keyEnumerator = new MyEnumerator();
			return keyEnumerator.map( k -> MapEntry.of( k, map.get( k ) ) );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return newMutableEnumerator();
		}

		private final class MyEnumerator extends AbstractEnumerator<K> implements MutableEnumerator.Decorator<K>
		{
			final MutableEnumerator<K> decoree;

			MyEnumerator()
			{
				decoree = keyList.newMutableEnumerator();
			}

			@Override public void deleteCurrent()
			{
				assert canMutateAssertion();
				K key = getCurrent();
				map.removeKey( key );
				decoree.deleteCurrent();
			}

			@Override public MutableEnumerator<K> getDecoratedUnmodifiableEnumerator()
			{
				return decoree;
			}
		}
	}

	private final MutableArraySet<K> keyList;
	private final MutableMap<K,V> map;
	final Hasher<? super K> keyHasher;
	private final MutableList<V> values;
	private final MyEntries entries;

	ConcreteMutableArrayHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		keyList = mutableCollections.newArraySet( initialCapacity, keyEqualityComparator );
		map = mutableCollections.newHashMap( initialCapacity, fillFactor, keyHasher, keyEqualityComparator, valueEqualityComparator );
		entries = new MyEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		//keys = new MutableArrayMapKeysList<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableArrayMapValuesList<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public MutableList<Binding<K,V>> mutableEntries()
	{
		return entries;
	}

	@Override public MutableArraySet<K> mutableKeys()
	{
		return keyList;
	}

	@Override public MutableList<V> mutableValues()
	{
		return values;
	}

	@Override public Hasher<? super K> getKeyHasher()
	{
		return keyHasher;
	}

	@Override public int size()
	{
		return map.size();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		return map.tryGetBindingByKey( key );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		Optional<V> existing = map.tryAdd( key, value );
		if( existing.isPresent() )
			return existing;
		keyList.add( key );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		return map.tryReplaceValue( key, value );
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		if( !map.tryRemoveKey( key ) )
			return false;
		keyList.remove( key );
		return true;
	}

	@Override public boolean clear()
	{
		if( !map.clear() )
			return false;
		boolean ok = keyList.clear();
		assert ok;
		return true;
	}
}
