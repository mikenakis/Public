package mikenakis.tyraki.mutable;

import mikenakis.kit.GenericException;
import mikenakis.kit.Hasher;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

/**
 * Hash Table.
 *
 * @author michael.gr
 */
public final class HashTable<K, T extends HashNode<K,T>> extends AbstractMutableEnumerable<T>
{
	private static final boolean EXTRA_CHECKING = false;

	private static class Bucket<K, T extends HashNode<K,T>>
	{
		private T head = null;
		private T tail = null;

		void add( T node )
		{
			assert tryFindNodeByKey( node.getKey() ) == null;
			if( head == null )
			{
				assert tail == null;
				head = tail = node;
				node.prev = node.next = null;
			}
			else
			{
				assert head.prev == null;
				head.prev = node;
				node.prev = null;
				node.next = head;
				head = node;
			}
		}

		void remove( T node )
		{
			assert mustBeValidAssertion( node );
			if( node.prev == null )
				head = node.next;
			else
				node.prev.next = node.next;
			if( node.next == null )
				tail = node.prev;
			else
				node.next.prev = node.prev;
		}

		@SuppressWarnings( "UnusedReturnValue" )
		boolean clear()
		{
			if( head == null )
			{
				assert tail == null;
				return false;
			}
			head = null;
			tail = null;
			return true;
		}

		@SuppressWarnings( "SameReturnValue" )
		private boolean mustBeValidAssertion( T node )
		{
			assert node.prev == null ? head == node : node.prev.next == node;
			assert node.next == null ? tail == node : node.next.prev == node;
			return true;
		}

		boolean isEmpty()
		{
			return head == null;
		}

		T tryFindNodeByKey( K key )
		{
			for( T node = head; node != null; node = node.next )
			{
				assert mustBeValidAssertion( node );
				if( node.keyEquals( key ) )
					return node;
			}
			return null;
		}

		int countNodes()
		{
			int count = 0;
			for( T node = head; node != null; node = node.next )
				count++;
			return count;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return countNodes() + " nodes";
		}
	}

	public final Hasher<? super K> keyHasher;
	public final float fillFactor;
	private int modificationCount = 0;
	private Bucket<K,T>[] buckets;
	private int hashNodeCount = 0;

	public HashTable( MutableCollections mutableCollections, Hasher<? super K> keyHasher, int initialCapacity, float fillFactor )
	{
		super( mutableCollections );
		assert fillFactor >= 0.1 && fillFactor <= 1.0;
		this.keyHasher = keyHasher;
		this.fillFactor = fillFactor;
		initialCapacity = getGreaterThanOrEqualPowerOfTwo( initialCapacity );
		buckets = allocateBuckets( initialCapacity );
	}

	void incrementModificationCount()
	{
		modificationCount++;
	}

	private static int getGreaterThanOrEqualPowerOfTwo( int n )
	{
		assert n > 0;
		n--;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return n + 1;
	}

	private static <K, T extends HashNode<K,T>> Bucket<K,T>[] allocateBuckets( int count )
	{
		Bucket<K,T>[] newBuckets = newArray( count, Bucket.class );
		for( int i = 0; i < count; i++ )
			newBuckets[i] = new Bucket<>();
		return newBuckets;
	}

	@Override public int countElements()
	{
		return hashNodeCount;
	}

	public int getLength()
	{
		return hashNodeCount;
	}

	public Optional<T> tryAdd( T hashNode )
	{
		assert mustBeWritableAssertion();
		ensureCapacity();
		Bucket<K,T> bucket = getBucket( hashNode );
		K key = hashNode.getKey();
		T existing = bucket.tryFindNodeByKey( key );
		if( existing != null )
			return Optional.of( existing );
		bucket.add( hashNode );
		hashNodeCount++;
		modificationCount++;
		if( EXTRA_CHECKING )
			assert mustBeValidAssertion();
		return Optional.empty();
	}

	private Bucket<K,T> getBucket( T hashNode )
	{
		int hashCode = hashNode.hashCode();
		assert hashCode == keyHasher.getHashCode( hashNode.getKey() ) : new GenericException( "Class used as key in hashMap appears to be mutable: " + hashNode.getKey().getClass().getName() );
		return getBucket( buckets, hashCode );
	}

	void remove( T hashNode )
	{
		assert mustBeWritableAssertion();
		Bucket<K,T> bucket = getBucket( hashNode );
		bucket.remove( hashNode );
		hashNodeCount--;
		modificationCount++;
		if( EXTRA_CHECKING )
			assert mustBeValidAssertion();
	}

	public T tryFindByKey( K key )
	{
		assert mustBeReadableAssertion();
		int hashCode = keyHasher.getHashCode( key );
		Bucket<K,T> bucket = getBucket( buckets, hashCode );
		return bucket.tryFindNodeByKey( key );
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( hashNodeCount == 0 )
			return false;
		for( Bucket<K,T> bucket : buckets )
			bucket.clear();
		hashNodeCount = 0;
		modificationCount++;
		if( EXTRA_CHECKING )
			assert mustBeValidAssertion();
		return true;
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		assert mustBeWritableAssertion();
		return new MyEnumerator<>( this );
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return new MyEnumerator<>( this );
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	private static class MyEnumerator<K, T extends HashNode<K,T>> implements MutableEnumerator.Defaults<T>
	{
		private final HashTable<K,T> hashTable;
		private int bucketIndex = 0;
		private T currentNode = null;
		private boolean deleted = false;
		private int modificationCount;

		protected MyEnumerator( HashTable<K,T> hashTable )
		{
			this.hashTable = hashTable;
			modificationCount = hashTable.modificationCount;
			moveToNextBucket();
		}

		private void moveToNextBucket()
		{
			for( ; bucketIndex < hashTable.buckets.length; bucketIndex++ )
			{
				Bucket<K,T> bucket = hashTable.buckets[bucketIndex];
				if( !bucket.isEmpty() )
				{
					currentNode = bucket.head;
					return;
				}
			}
		}

		@Override public boolean isFinished()
		{
			assert hashTable.mustBeReadableAssertion();
			//assert ownerModificationCount == modificationCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			return currentNode == null;
		}

		@Override public T current()
		{
			assert hashTable.mustBeReadableAssertion();
			assert modificationCount == hashTable.modificationCount : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			assert !deleted : new IllegalStateException();
			return currentNode;
		}

		@Override public UnmodifiableEnumerator<T> moveNext()
		{
			assert hashTable.mustBeReadableAssertion();
			assert modificationCount == hashTable.modificationCount : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			deleted = false;
			currentNode = currentNode.next;
			if( currentNode == null )
			{
				bucketIndex++;
				moveToNextBucket();
			}
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert hashTable.mustBeWritableAssertion();
			assert modificationCount == hashTable.modificationCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert currentNode != null : new IllegalStateException();
			hashTable.buckets[bucketIndex].remove( currentNode );
			hashTable.hashNodeCount--;
			deleted = true;
			hashTable.modificationCount++;
			modificationCount++;
			if( EXTRA_CHECKING )
				assert hashTable.mustBeValidAssertion();
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return currentNode == null ? "finished!" : modificationCount == hashTable.modificationCount ? "concurrent modification!" : Objects.toString( currentNode );
		}
	}

	private int countHashNodes()
	{
		int count = 0;
		for( Bucket<K,T> bucket : buckets )
			count += bucket.countNodes();
		return count;
	}

	@SuppressWarnings( "SameReturnValue" )
	private boolean mustBeValidAssertion()
	{
		int actualHashNodeCount = countHashNodes();
		assert hashNodeCount == actualHashNodeCount;
		return true;
	}

	private static <T> T[] newArray( int count, Class<? super T> jvmClass )
	{
		@SuppressWarnings( "unchecked" )
		T[] result = (T[])Array.newInstance( jvmClass, count );
		return result;
	}

	private void ensureCapacity()
	{
		if( hashNodeCount < buckets.length * fillFactor )
			return;
		T[] allHashNodes = getAllHashNodes();
		buckets = allocateBuckets( buckets.length << 1 );
		for( T hashNode : allHashNodes )
		{
			Bucket<K,T> bucket = getBucket( hashNode );
			bucket.add( hashNode );
		}
		modificationCount++;
		if( EXTRA_CHECKING )
			assert mustBeValidAssertion();
	}

	private T[] getAllHashNodes()
	{
		T[] allHashNodes = newArray( hashNodeCount, HashNode.class );
		int i = 0;
		for( T hashNode : this )
			allHashNodes[i++] = hashNode;
		return allHashNodes;
	}

	private static int mangleHashCode( int hashCode )
	{
		return hashCode ^ (hashCode >>> 16);
	}

	private static <K, T extends HashNode<K,T>> Bucket<K,T> getBucket( Bucket<K,T>[] buckets, int hashCode )
	{
		assert isPowerOfTwo( buckets.length );
		int index = mangleHashCode( hashCode ) & (buckets.length - 1);
		return buckets[index];
	}

	private static boolean isPowerOfTwo( int x )
	{
		assert x != 0; //normally this is not a power of two, but we don't even want to have to check.
		return (x & (x - 1)) == 0;
	}
}
