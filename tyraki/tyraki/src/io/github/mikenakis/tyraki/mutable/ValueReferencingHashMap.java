package io.github.mikenakis.tyraki.mutable;

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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Optional;

/**
 * Value Referencing (Strong/Soft/Weak) Hash Map.
 * <p>
 * TODO: need to expunge stale entries every once in a while! TODO: research the reference queue that can be passed to the constructor of WeakReference!
 *
 * @author michael.gr
 */
final class ValueReferencingHashMap<K, V> extends AbstractMutableMap<K,V> implements MutableHashMap.Defaults<K,V>
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
			return hashTable.newMutableEnumerator().map( converter ).mutableFilter( k -> k != null );
		}

		@Override public UnmodifiableEnumerator<Binding<K,V>> newUnmodifiableEnumerator()
		{
			assert mustBeReadableAssertion();
			return hashTable.newUnmodifiableEnumerator().map( converter ).filter( k -> k != null );
		}

		private final Function1<Binding<K,V>,Node> converter = node -> //
		{
			V value = node.valueReference.get();
			if( value == null )
				return null;
			return MapEntry.of( node.key, value );
		};
	}

	private final HashTable<K,Node> hashTable;
	private int modificationCount = 0;
	final Hasher<? super K> keyHasher;
	final ReferencingMethod referencingMethod;
	final ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();
	private final MutableCollection<K> keys;
	private final MutableCollection<V> values;
	private final MyEntries entries;

	ValueReferencingHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, ReferencingMethod referencingMethod, //
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

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert mustBeReadableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return Optional.empty();
		V value = node.valueReference.get();
		if( value == null )
			return Optional.empty();
		return Optional.of( MapEntry.of( node.key, value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node1 = hashTable.tryFindByKey( key );
		if( node1 != null )
		{
			V existing = node1.valueReference.get();
			if( existing != null )
				return Optional.of( existing ); //key already exists and has a value.
			node1.valueReference = Helpers.newReference( referencingMethod, value, referenceQueue );
			modificationCount++;
		}
		else
		{
			Node node2 = new Node( key, value );
			Optional<Node> existing = hashTable.tryAdd( node2 );
			assert existing.isEmpty();
		}
		return Optional.empty();
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert mustBeWritableAssertion();
		Node node = hashTable.tryFindByKey( key );
		if( node == null )
			return false;
		node.valueReference = Helpers.newReference( referencingMethod, value, referenceQueue );
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

	private class Node extends HashNode<K,Node> //implements Binding<K,V>
	{
		final K key;
		Reference<V> valueReference;

		Node( K key, V value )
		{
			assert key != null;
			this.key = key;
			valueReference = Helpers.newReference( referencingMethod, value, referenceQueue );
		}

		public boolean equals( Node otherNode )
		{
			if( !keys().getEqualityComparator().equals( key, otherNode.key ) )
				return false;
			if( !values().getEqualityComparator().equals( valueReference.get(), otherNode.valueReference.get() ) )
				return false;
			return true;
		}

		public boolean equals( Binding<K,V> other )
		{
			if( !keys().getEqualityComparator().equals( key, other.getKey() ) )
				return false;
			if( !values().getEqualityComparator().equals( valueReference.get(), other.getValue() ) )
				return false;
			return true;
		}

		@Deprecated @Override public boolean equals( Object other )
		{
			if( other instanceof ValueReferencingHashMap<?,?>.Node )
			{
				@SuppressWarnings( "unchecked" ) Node otherNode = (Node)other;
				return equals( otherNode );
			}
			if( other instanceof Binding<?,?> )
			{
				@SuppressWarnings( "unchecked" ) Binding<K,V> otherBinding = (Binding<K,V>)other;
				return equals( otherBinding );
			}
			return false;
		}

		@Override public int hashCode()
		{
			return hashTable.keyHasher.getHashCode( key );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "{ " + key + " -> " + valueReference.get() + " }";
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
}
