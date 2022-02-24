package mikenakis.tyraki.mutable;

import mikenakis.tyraki.Binding;
import mikenakis.tyraki.FreezableArrayHashMap;
import mikenakis.tyraki.FreezableArraySet;
import mikenakis.tyraki.FreezableMap;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableArraySet;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.UnmodifiableArrayHashMap;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;

import java.util.Optional;

/**
 * Array Hash Map.
 *
 * @author michael.gr
 */
//IntellijIdea blooper: "Class 'FreezableMutableArrayHashMap' must either be declared abstract or implement abstract method 'getEntries()' in 'UnmodifiableArrayMap'"
final class FreezableMutableArrayHashMap<K, V> extends AbstractMutableMap<K,V> implements FreezableArrayHashMap.Defaults<K,V>
{
	private final class MyEntries extends AbstractMutableEntries implements MutableList.Defaults<Binding<K,V>>
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public boolean isFrozen()
		{
			return FreezableMutableArrayHashMap.this.isFrozen();
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

		private final class MyEnumerator extends AbstractMutableEnumerator<K> implements MutableEnumerator.Decorator<K>
		{
			final MutableEnumerator<K> decoree;

			MyEnumerator()
			{
				super( FreezableMutableArrayHashMap.this.mutableCollections );
				decoree = keyList.newMutableEnumerator();
			}

			@Override public void deleteCurrent()
			{
				assert canWriteAssertion();
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

	private boolean frozen = false;
	private final FreezableArraySet<K> keyList;
	private final FreezableMap<K,V> map;
	final Hasher<? super K> keyHasher;
	private final MutableList<V> values;
	private final MyEntries entries;

	FreezableMutableArrayHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
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

	@Override public boolean isFrozen()
	{
		return frozen;
	}

	@Override public void freeze()
	{
		assert !frozen;
		keyList.freeze();
		map.freeze();
		frozen = true;
	}

	@Override public UnmodifiableArrayHashMap<K,V> frozen()
	{
		freeze();
		return this;
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
