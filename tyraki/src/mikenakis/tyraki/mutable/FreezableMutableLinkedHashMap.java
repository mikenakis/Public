package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.FreezableHashMap;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableHashMap;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

/**
 * Linked Hash Map.
 *
 * @author michael.gr
 */
class FreezableMutableLinkedHashMap<K, V> extends AbstractMutableMap<K,V> implements FreezableHashMap.Defaults<K,V>
{
	private final class MyEntries extends AbstractMutableEntries
	{
		MyEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return hashTable.getModificationCount();
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			assert canReadAssertion();
			return new MyEnumerator().converted( converter );
		}

		private final Function1<Binding<K,V>,Item> converter = item -> MapEntry.of( item.key, item.value );
	}

	private boolean frozen = false;
	private final HashTable<K,Item> hashTable;
	final Hasher<? super K> keyHasher;
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;
	private Item head = null;
	private Item tail = null;

	FreezableMutableLinkedHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		hashTable = new HashTable<>( mutableCollections, keyHasher, initialCapacity, fillFactor );
		entries = new MyEntries( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		keys = new MutableMapKeysCollection<>( mutableCollections, this, keyEqualityComparator );
		values = new MutableMapValuesCollection<>( mutableCollections, this, valueEqualityComparator );
	}

	@Override public boolean isFrozen()
	{
		return frozen;
	}

	@Override public void freeze()
	{
		assert !frozen;
		hashTable.freeze();
		frozen = true;
	}

	@Override public UnmodifiableHashMap<K,V> frozen()
	{
		freeze();
		return this;
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
		assert isReadableAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert isReadableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return Optional.empty();
		assert isValidAssertion( item );
		return Optional.of( MapEntry.of( item.key, item.value ) );
	}

	@Override public boolean tryAdd( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item != null )
		{
			assert isValidAssertion( item );
			return false; //key already exists.
		}
		item = new Item( key, value );
		boolean ok = hashTable.tryAdd( item );
		assert ok;
		if( tail == null )
		{
			assert head == null;
			head = tail = item;
			item.prevInMap = item.nextInMap = null;
		}
		else
		{
			assert tail.nextInMap == null;
			tail.nextInMap = item;
			item.nextInMap = null;
			item.prevInMap = tail;
			tail = item;
		}
		assert isValidAssertion( item );
		return true;
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return false;
		assert isValidAssertion( item );
		item.value = value;
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert isWritableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return false;
		hashTable.remove( item );
		assert isValidAssertion( item );
		if( item.prevInMap == null )
			head = item.nextInMap;
		else
			item.prevInMap.nextInMap = item.nextInMap;
		if( item.nextInMap == null )
			tail = item.prevInMap;
		else
			item.nextInMap.prevInMap = item.prevInMap;
		return true;
	}

	@Override public boolean clear()
	{
		assert isWritableAssertion();
		if( !hashTable.clear() )
		{
			assert head == null;
			assert tail == null;
			return false;
		}
		head = null;
		tail = null;
		return true;
	}

	@Override public UnmodifiableCollection<K> keys()
	{
		return keys;
	}

	@Override public UnmodifiableCollection<V> values()
	{
		return values;
	}

	@SuppressWarnings( "SameReturnValue" ) private boolean isValidAssertion( Item item )
	{
		assert item.prevInMap == null? head == item : item.prevInMap.nextInMap == item;
		assert item.nextInMap == null? tail == item : item.nextInMap.prevInMap == item;
		return true;
	}

	private class Item extends HashNode<K,Item> //implements Binding<K,V>
	{
		final K key;
		V value;
		Item prevInMap;
		Item nextInMap;

		Item( K key, V value )
		{
			assert key != null;
			this.key = key;
			this.value = value;
		}

		public boolean equals( Item other )
		{
			if( !keys().getEqualityComparator().equals( key, other.key ) )
				return false;
			if( !values().getEqualityComparator().equals( value, other.value ) )
				return false;
			return true;
		}

		public boolean equals( Binding<K,V> other )
		{
			if( !keys().getEqualityComparator().equals( key, other.getKey() ) )
				return false;
			if( !values().getEqualityComparator().equals( value, other.getValue() ) )
				return false;
			return true;
		}

		@Override public boolean equals( Object other )
		{
			if( other instanceof FreezableMutableLinkedHashMap<?,?>.Item )
			{
				@SuppressWarnings( "unchecked" )
				Item otherItem = (Item)other;
				return equals( otherItem );
			}
			if( other instanceof Binding<?,?> )
			{
				@SuppressWarnings( "unchecked" )
				Binding<K,V> otherBinding = (Binding<K,V>)other;
				return equals( otherBinding );
			}
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return hashTable.keyHasher.getHashCode( key );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "{ " + key + " -> " + value + " }";
		}

		@Override public K getKey()
		{
			return key;
		}

		@Override public boolean keyEquals( K otherKey )
		{
			return keys().getEqualityComparator().equals( key, otherKey );
		}
	}

	private class MyEnumerator implements MutableEnumerator.Defaults<Item>
	{
		Item currentNode;
		boolean deleted = false;
		int modificationCount = hashTable.getModificationCount();

		protected MyEnumerator()
		{
			currentNode = head;
		}

		@Override public boolean isFinished()
		{
			//assert ownerModificationCount == modificationCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			return currentNode == null;
		}

		@Override public Item getCurrent()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			assert !deleted : new IllegalStateException();
			assert isValidAssertion( currentNode );
			return currentNode;
		}

		@Override public UnmodifiableEnumerator<Item> moveNext()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			deleted = false;
			currentNode = currentNode.nextInMap;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert currentNode != null : new IllegalStateException();
			assert isValidAssertion( currentNode );
			removeKey( currentNode.key );
			deleted = true;
			modificationCount++;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return currentNode == null ? "finished!" : modificationCount == hashTable.getModificationCount()? Objects.toString( currentNode ) : "concurrent modification!";
		}
	}
}
