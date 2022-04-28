package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * A Set implemented using a {@link java.util.HashSet}.
 *
 * @author michael.gr
 */
final class ConcreteMutableHashSet<E> extends AbstractMutableCollection<E> implements MutableHashSet.Defaults<E>
{
	private final HashTable<E,Item> hashTable;

	ConcreteMutableHashSet( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		hashTable = new HashTable<>( mutableCollections, hasher, initialCapacity, fillFactor );
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return hashTable.keyHasher;
	}

	@Override public int size()
	{
		assert mustBeReadableAssertion();
		return hashTable.getLength();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		assert mustBeReadableAssertion();
		Item item = hashTable.tryFindByKey( element );
		if( item == null )
			return Optional.empty();
		return Optional.of( item.element );
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert mustBeWritableAssertion();
		Item myItem = new Item( element );
		return hashTable.tryAdd( myItem ).map( existing -> existing.element );
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert mustBeWritableAssertion();
		Item oldItem = hashTable.tryFindByKey( oldElement );
		if( oldItem == null )
			return false;
		if( hashTable.tryFindByKey( newElement ) != null )
			return false;
		hashTable.remove( oldItem );
		Item newItem = new Item( newElement );
		Optional<Item> existing = hashTable.tryAdd( newItem );
		assert existing.isEmpty();
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert mustBeWritableAssertion();
		Item oldItem = hashTable.tryFindByKey( element );
		if( oldItem == null )
			return false;
		hashTable.remove( oldItem );
		return true;
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		return hashTable.clear();
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert mustBeReadableAssertion();
		return hashTable.newMutableEnumerator().map( item -> item.element );
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return hashTable.newUnmodifiableEnumerator().map( item -> item.element );
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
			assert element != null;
			this.element = element;
		}

		public boolean equals( Item other )
		{
			return equalityComparator.equals( element, other.element );
		}

		@Override public boolean equals( Object o )
		{
			if( o instanceof ConcreteMutableHashSet<?>.Item )
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
