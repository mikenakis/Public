package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.FreezableHashSet;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableHashSet;
import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;

import java.util.Optional;

/**
 * A Set implemented using a {@link java.util.HashSet}.
 *
 * @author michael.gr
 */
final class FreezableMutableHashSet<E> extends AbstractMutableCollection<E> implements FreezableHashSet.Defaults<E>
{
	private boolean frozen = false;
	private final HashTable<E,Item> hashTable;

	FreezableMutableHashSet( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		hashTable = new HashTable<>( mutableCollections, hasher, initialCapacity, fillFactor );
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

	@Override public UnmodifiableHashSet<E> frozen()
	{
		freeze();
		return this;
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return hashTable.keyHasher;
	}

	@Override public int size()
	{
		assert canReadAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		assert canReadAssertion();
		Item item = hashTable.tryFindByKey( element );
		if( item == null )
			return Optional.empty();
		return Optional.of( item.element );
	}

	@Override public boolean tryAdd( E element )
	{
		assert canWriteAssertion();
		Item myItem = new Item( element );
		return hashTable.tryAdd( myItem );
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert canWriteAssertion();
		Item oldItem = hashTable.tryFindByKey( oldElement );
		if( oldItem == null )
			return false;
		if( hashTable.tryFindByKey( newElement ) != null )
			return false;
		hashTable.remove( oldItem );
		Item newItem = new Item( newElement );
		boolean ok = hashTable.tryAdd( newItem );
		assert ok;
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert canWriteAssertion();
		Item oldItem = hashTable.tryFindByKey( element );
		if( oldItem == null )
			return false;
		hashTable.remove( oldItem );
		return true;
	}

	@Override public boolean clear()
	{
		assert canWriteAssertion();
		return hashTable.clear();
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		return hashTable.newMutableEnumerator().converted( item -> item.element );
	}

	@Override public int getModificationCount()
	{
		return hashTable.getModificationCount();
	}

	private final class Item extends HashNode<E,Item>
	{
		final E element;
		int hashCode = 0;

		Item( E element )
		{
			this.element = element;
		}

		public boolean equals( Item other )
		{
			return equalityComparator.equals( element, other.element );
		}

		@Override public boolean equals( Object o )
		{
			if( o instanceof FreezableMutableHashSet<?>.Item )
			{
				@SuppressWarnings( "unchecked" )
				Item otherItem = (Item)o;
				return equals( otherItem );
			}
			assert false;
			return false;
		}

		@SuppressWarnings( "NonFinalFieldReferencedInHashCode" )
		@Override public int hashCode()
		{
			if( hashCode == 0 )
				hashCode = hashTable.keyHasher.getHashCode( element );
			assert hashCode == hashTable.keyHasher.getHashCode( element );
			return hashCode;
		}

		@Override public E getKey()
		{
			return element;
		}

		@Override public boolean keyEquals( E key )
		{
			return equalityComparator.equals( element, key );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return String.valueOf( element );
		}
	}
}