package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

/**
 * Linked Hash Map.
 *
 * @author michael.gr
 */
class ConcreteMutableLinkedHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
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
			return new MyEnumerator().map( converter );
		}

		private final Function1<Binding<K,V>,Item> converter = item -> MapEntry.of( item.key, item.value );
	}

	private final HashTable<K,Item> hashTable;
	final Hasher<? super K> keyHasher;
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;
	private Item head = null;
	private Item tail = null;

	ConcreteMutableLinkedHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher,
		EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		hashTable = new HashTable<>( mutableCollections, keyHasher, initialCapacity, fillFactor );
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
		assert canReadAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert canReadAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return Optional.empty();
		assert isValidAssertion( item );
		return Optional.of( MapEntry.of( item.key, item.value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert canMutateAssertion();
		Item item1 = hashTable.tryFindByKey( key );
		if( item1 != null )
		{
			assert isValidAssertion( item1 );
			return Optional.of( item1.value ); //key already exists.
		}
		Item item2 = new Item( key, value );
		Optional<Item> existing = hashTable.tryAdd( item2 );
		assert existing.isEmpty();
		if( tail == null )
		{
			assert head == null;
			head = tail = item2;
			item2.prevInMap = item2.nextInMap = null;
		}
		else
		{
			assert tail.nextInMap == null;
			tail.nextInMap = item2;
			item2.nextInMap = null;
			item2.prevInMap = tail;
			tail = item2;
		}
		assert isValidAssertion( item2 );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert canMutateAssertion();
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
		assert canMutateAssertion();
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
		assert canMutateAssertion();
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
			if( other instanceof ConcreteMutableLinkedHashMap<?,?>.Item )
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
