package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableHashMap;

import java.util.Optional;

/**
 * Hash Map.
 *
 * @author michael.gr
 */
abstract class AbstractMutableHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
{
	protected class MutableHashEntries extends AbstractMutableEntries
	{
		MutableHashEntries( MutableCollections mutableCollections, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
		{
			super( mutableCollections, keyEqualityComparator, valueEqualityComparator );
		}

		@Override public int getModificationCount()
		{
			return hashTable.getModificationCount();
		}

		@Override public MutableEnumerator<Binding<K,V>> newMutableEnumerator()
		{
			return MutableEnumerator.downCast( hashTable.newMutableEnumerator() );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			return UnmodifiableEnumerator.downCast( hashTable.newUnmodifiableEnumerator() );
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final HashTable<K,Node<K,V>> hashTable;

	AbstractMutableHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher )
	{
		super( mutableCollections );
		hashTable = new HashTable<>( mutableCollections, keyHasher, initialCapacity, fillFactor );
	}

	@Override public Hasher<? super K> getKeyHasher()
	{
		return hashTable.keyHasher;
	}

	@Override public int size()
	{
		assert mustBeReadableAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		return Optional.ofNullable( hashTable.tryFindByKey( key ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node<K,V> node = newItem( key, value );
		return hashTable.tryAdd( node ).map( existing -> existing.value );
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node<K,V> node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		node.value = value;
		hashTable.incrementModificationCount();
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node<K,V> node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		hashTable.remove( node );
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		return hashTable.clear();
	}

	private Node<K,V> newItem( K key, V value )
	{
		return new Node<>( this, key, value );
	}

	private  static class Node<K,V> extends HashNode<K,Node<K,V>> implements Binding<K,V>
	{
		private final UnmodifiableHashMap<K,V> hashMap;
		private final K key;
		public V value;
		private int hashCode = 0;

		Node( UnmodifiableHashMap<K,V> hashMap, K key, V value )
		{
			assert key != null;
			this.hashMap = hashMap;
			this.key = key;
			this.value = value;
		}

		public boolean equals( Node<K,V> other )
		{
			if( !hashMap.keys().getEqualityComparator().equals( key, other.key ) )
				return false;
			if( !hashMap.values().getEqualityComparator().equals( value, other.value ) )
				return false;
			return true;
		}

		public boolean equals( Binding<K,V> other )
		{
			if( !hashMap.keys().getEqualityComparator().equals( key, other.getKey() ) )
				return false;
			if( !hashMap.values().getEqualityComparator().equals( value, other.getValue() ) )
				return false;
			return true;
		}

		@Override public boolean equals( Object other )
		{
			if( other instanceof Node<?,?> otherNode )
				return equals( otherNode );
			if( other instanceof Binding<?,?> )
			{
				@SuppressWarnings( "unchecked" )
				Binding<K,V> otherBinding = (Binding<K,V>)other;
				return equals( otherBinding );
			}
			assert false;
			return false;
		}

		@SuppressWarnings( "NonFinalFieldReferencedInHashCode" )
		@Override public int hashCode()
		{
			if( hashCode == 0 )
				hashCode = hashMap.getKeyHasher().getHashCode( key );
			assert hashMap.getKeyHasher().getHashCode( key ) == hashCode;
			return hashCode;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "{ " + key + " -> " + value + " }";
		}

		@Override public K getKey()
		{
			return key;
		}

		@Override public V getValue()
		{
			return value;
		}

		@Override public boolean keyEquals( K otherKey )
		{
			return hashMap.keys().getEqualityComparator().equals( key, otherKey );
		}
	}
}
