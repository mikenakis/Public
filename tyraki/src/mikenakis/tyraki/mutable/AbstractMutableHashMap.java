package mikenakis.tyraki.mutable;

import mikenakis.kit.Hasher;
import mikenakis.tyraki.Binding;
import mikenakis.tyraki.HashMapNode;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashMap;
import mikenakis.kit.EqualityComparator;

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

		@Override public boolean isFrozen()
		{
			return AbstractMutableHashMap.this.isFrozen();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private final HashTable<K,HashMapNode<K,V>> hashTable;

	AbstractMutableHashMap( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super K> keyHasher )
	{
		super( mutableCollections );
		hashTable = new HashTable<>( mutableCollections, keyHasher, initialCapacity, fillFactor );
	}

	public final void freeze()
	{
		hashTable.freeze();
	}

	@Override public final boolean isFrozen()
	{
		return hashTable.isFrozen();
	}

	@Override public Hasher<? super K> getKeyHasher()
	{
		return hashTable.keyHasher;
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
		return Optional.ofNullable( hashTable.tryFindByKey( key ) );
	}

	@Override public boolean tryAdd( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		HashMapNode<K,V> item = newItem( key, value );
		return hashTable.tryAdd( item );
	}

	@Override public boolean tryReplaceValue( K key, V value )
	{
		assert key != null;
		assert isWritableAssertion();
		HashMapNode<K,V> item = hashTable.tryFindByKey( key );
		if( item == null )
			return false;
		item.value = value;
		hashTable.incrementModificationCount();
		return true;
	}

	@Override public boolean tryRemoveKey( K key )
	{
		assert key != null;
		assert isWritableAssertion();
		HashMapNode<K,V> item = hashTable.tryFindByKey( key );
		if( item == null )
			return false;
		hashTable.remove( item );
		return true;
	}

	@Override public boolean clear()
	{
		assert isWritableAssertion();
		return hashTable.clear();
	}

	protected HashMapNode<K,V> newItem( K key, V value )
	{
		return new HashMapNode<>( this, key, value );
	}
}
