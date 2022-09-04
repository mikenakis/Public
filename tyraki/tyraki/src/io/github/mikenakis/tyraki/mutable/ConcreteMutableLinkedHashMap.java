package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableHashMap;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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
			assert mustBeReadableAssertion();
			return new MyEnumerator().map( converter );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			assert mustBeReadableAssertion();
			return newMutableEnumerator();
		}

		private final Function1<Binding<K,V>,Node> converter = node -> MapEntry.of( node.key, node.value );
	}

	private final HashTable<K,Node> hashTable;
	private final Hasher<? super K> keyHasher;
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;
	private Node head = null;
	private Node tail = null;

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
		assert mustBeReadableAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return Optional.empty();
		assert mustBeValidAssertion( node );
		return Optional.of( MapEntry.of( node.key, node.value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
//		Optional<V> result = tryAdd0( key, value );
//		Log.debug( "key=" + key + ", value=" + value + ", result=" + result );
//		return result;
//	}
//
//	private Optional<V> tryAdd0( K key, V value )
//	{
		Node node1 = hashTable.tryFindByKey( key );
		if( node1 != null )
		{
			assert mustBeValidAssertion( node1 );
			return Optional.of( node1.value ); //key already exists.
		}
		Node node2 = new Node( key, value );
		Optional<Node> existing = hashTable.tryAdd( node2 );
		assert existing.isEmpty();
		if( tail == null )
		{
			assert head == null;
			head = tail = node2;
			node2.prevInMap = node2.nextInMap = null;
		}
		else
		{
			assert tail.nextInMap == null;
			tail.nextInMap = node2;
			node2.nextInMap = null;
			node2.prevInMap = tail;
			tail = node2;
		}
		assert mustBeValidAssertion( node2 );
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		assert mustBeValidAssertion( node );
		node.value = value;
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		hashTable.remove( node );
		assert mustBeValidAssertion( node );
		if( node.prevInMap == null )
			head = node.nextInMap;
		else
			node.prevInMap.nextInMap = node.nextInMap;
		if( node.nextInMap == null )
			tail = node.prevInMap;
		else
			node.nextInMap.prevInMap = node.prevInMap;
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
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

	@SuppressWarnings( "SameReturnValue" ) private boolean mustBeValidAssertion( Node node )
	{
		assert node.prevInMap == null? head == node : node.prevInMap.nextInMap == node;
		assert node.nextInMap == null? tail == node : node.nextInMap.prevInMap == node;
		return true;
	}

	private class Node extends HashNode<K,Node> //implements Binding<K,V>
	{
		final K key;
		V value;
		Node prevInMap;
		Node nextInMap;

		Node( K key, V value )
		{
			assert key != null;
			this.key = key;
			this.value = value;
		}

		public boolean equals( Node otherNode )
		{
			if( !keys().getEqualityComparator().equals( key, otherNode.key ) )
				return false;
			if( !values().getEqualityComparator().equals( value, otherNode.value ) )
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
			if( other instanceof ConcreteMutableLinkedHashMap<?,?>.Node )
			{
				@SuppressWarnings( "unchecked" ) Node otherNode = (Node)other;
				return equals( otherNode );
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

	private class MyEnumerator implements MutableEnumerator.Defaults<Node>
	{
		Node currentNode;
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

		@Override public Node current()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			assert !deleted : new IllegalStateException();
			assert mustBeValidAssertion( currentNode );
			return currentNode;
		}

		@Override public UnmodifiableEnumerator<Node> moveNext()
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
			assert mustBeValidAssertion( currentNode );
			removeKey( currentNode.key );
			deleted = true;
			modificationCount++;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return currentNode == null ? "finished!" : modificationCount == hashTable.getModificationCount()? Objects.toString( currentNode ) : "concurrent modification!";
		}

		@Override public Coherence coherence()
		{
			return ConcreteMutableLinkedHashMap.this.coherence();
		}
	}
}
