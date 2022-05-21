package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.Binding;
import io.github.mikenakis.tyraki.MutableCollection;
import io.github.mikenakis.tyraki.MutableHashMap;
import io.github.mikenakis.tyraki.MapEntry;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Objects;
import java.util.Optional;

/**
 * Key Referencing (Strong/Soft/Weak) Hash Map.
 *
 * TODO: need to expunge stale entries every once in a while! TODO: research the reference queue that can be passed to the constructor of WeakReference!
 *
 * @author michael.gr
 */
final class KeyReferencingHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
{
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
			return hashTable.newMutableEnumerator().map( converter ).mutableFilter( obj -> Objects.nonNull( obj ) );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			assert mustBeReadableAssertion();
			return hashTable.newUnmodifiableEnumerator().map( converter ).filter( obj -> Objects.nonNull( obj ) );
		}

		private final Function1<Binding<K,V>,Node> converter = node ->
		{
			K key = node.keyReference.get();
			if( key == null )
				return null;
			return MapEntry.of( key, node.value );
		};
	}

	private final HashTable<K,Node> hashTable;
	private int modificationCount = 0;
	final Hasher<? super K> keyHasher;
	final ReferencingMethod referencingMethod;
	final ReferenceQueue<K> referenceQueue = new ReferenceQueue<>();
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;

	KeyReferencingHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, ReferencingMethod referencingMethod,
		Hasher<? super K> keyHasher, EqualityComparator<? super K> keyEqualityComparator, EqualityComparator<? super V> valueEqualityComparator )
	{
		super( mutableCollections );
		this.keyHasher = keyHasher;
		this.referencingMethod = referencingMethod;
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

	@Override public V get( K key )
	{
		assert key != null;
		assert false; //please do not invoke this function; invoke tryGet() instead.
		return null;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return Optional.empty();
		K newKey = node.keyReference.get();
		if( newKey == null )
			return Optional.empty();
		return Optional.of( MapEntry.of( newKey, node.value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node = new Node( key, value );
		return hashTable.tryAdd( node ).map( previous -> previous.value );
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		if( values.getEqualityComparator().equals( node.value, value ) )
			return false;
		node.value = value;
		modificationCount++;
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
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		for( ; ; ) // clear reference queue. We don't need to expunge entries since table is getting cleared.
			if( referenceQueue.poll() == null )
				break;
		if( !hashTable.clear() )
			return false;
		for( ; ; ) //clearing the map may have caused GC, which may have caused additional entries to go stale, so clear reference queue again.
			if( referenceQueue.poll() == null )
				break;
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

//    private void expungeStaleEntries()
//    {
//        for( Reference<? extends K> x; (x = referenceQueue.poll()) != null; )
//        {
//            synchronized( referenceQueue )
//            {
//                Item<K> item = new Item<>( this, x.invoke() );
//                @SuppressWarnings( "unchecked" )
//                Binding<K,V> e = (Binding<K,V>)x;
//                int i = indexFor( e.hash, table.length );
//
//                java.util.WeakHashMap.Entry<K,V> prev = table[i];
//                java.util.WeakHashMap.Entry<K,V> p = prev;
//                while( p != null )
//                {
//                    java.util.WeakHashMap.Entry<K,V> next = p.next;
//                    if( p == e )
//                    {
//                        if( prev == e )
//                            table[i] = next;
//                        else
//                            prev.next = next;
//                        // Must not null out e.next;
//                        // stale entries may be in use by a HashIterator
//                        e.value = null; // Help GC
//                        size--;
//                        break;
//                    }
//                    prev = p;
//                    p = next;
//                }
//            }
//        }
//    }

	private class Node extends HashNode<K,Node>
	{
		final Reference<K> keyReference;
		final int hashCode;
		V value;

		Node( K key, V value )
		{
			assert key != null;
			keyReference = Helpers.newReference( referencingMethod, key, referenceQueue );
			hashCode = keyHasher.getHashCode( key );
			this.value = value;
		}

		boolean equals( Node other )
		{
			K otherKey = other.keyReference.get();
			return keyEquals( otherKey );
		}

		@Override public boolean equals( Object other )
		{
			if( other == this )
				return true;
			if( other == null )
				return false;
			if( other instanceof KeyReferencingHashMap<?,?>.Node )
			{
				@SuppressWarnings( "unchecked" ) Node otherNode = (Node)other;
				return equals( otherNode );
			}
			assert false;
			return false;
		}

		@Override public int hashCode()
		{
			return hashCode;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			K key = keyReference.get();
			if( key == null )
				return "<stale>";
			return String.valueOf( key );
		}

		@Override public K getKey()
		{
			return keyReference.get();
		}

		@Override public boolean keyEquals( K otherKey )
		{
			K key = keyReference.get();
			return keys().getEqualityComparator().equals( key, otherKey );
		}
	}
}
