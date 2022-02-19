package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.MapEntry;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;

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
			assert canReadAssertion();
			return hashTable.newMutableEnumerator().map( converter ).mutableFiltered( obj -> Objects.nonNull( obj ) );
		}

		private final Function1<Binding<K,V>,Item> converter = item ->
		{
			K key = item.keyReference.get();
			if( key == null )
				return null;
			return MapEntry.of( key, item.value );
		};
	}

	private final HashTable<K,Item> hashTable;
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

	@Override public boolean isFrozen()
	{
		return false;
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

	@Override public V get( K key )
	{
		assert key != null;
		assert false; //please do not invoke this function; invoke tryGet() instead.
		return null;
	}

	@Override public Optional<Binding<K,V>> tryGetBindingByKey( K key )
	{
		assert key != null;
		assert isReadableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return Optional.empty();
		K newKey = item.keyReference.get();
		if( newKey == null )
			return Optional.empty();
		return Optional.of( MapEntry.of( newKey, item.value ) );
	}

	@Override public Optional<V> tryAdd( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		Item item = new Item( key, value );
		return hashTable.tryAdd( item ).map( previous -> previous.value );
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		Item item = hashTable.tryFindByKey( key );
		if( item == null )
			return false;
		if( values.getEqualityComparator().equals( item.value, value ) )
			return false;
		item.value = value;
		modificationCount++;
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
		return true;
	}

	@Override public boolean clear()
	{
		assert isWritableAssertion();
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

	private class Item extends HashNode<K,Item>
	{
		final Reference<K> keyReference;
		final int hashCode;
		V value;

		Item( K key, V value )
		{
			assert key != null;
			keyReference = Helpers.newReference( referencingMethod, key, referenceQueue );
			hashCode = keyHasher.getHashCode( key );
			this.value = value;
		}

		boolean equals( Item other )
		{
			K otherKey = other.keyReference.get();
			return keyEquals( otherKey );
		}

		@Override public boolean equals( Object o )
		{
			if( o == this )
				return true;
			if( o == null )
				return false;
			if( o instanceof KeyReferencingHashMap<?,?>.Item )
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
