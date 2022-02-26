package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

/**
 * A Linked {@link MutableHashSet}.
 *
 * @author michael.gr
 */
final class ConcreteMutableLinkedHashSet<E> extends AbstractMutableCollection<E> implements MutableHashSet.Defaults<E>
{
	private final HashTable<E,Item> hashTable;
	private Item head;
	private Item tail;

	ConcreteMutableLinkedHashSet( MutableCollections mutableCollections, int initialCapacity, float fillFactor, Hasher<? super E> hasher, EqualityComparator<? super E> equalityComparator )
	{
		super( mutableCollections, equalityComparator );
		hashTable = new HashTable<>( mutableCollections, hasher, initialCapacity, fillFactor );
	}

	@Override public Hasher<? super E> getElementHasher()
	{
		return hashTable.keyHasher;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		assert canReadAssertion();
		return new MyEnumerator().map( item -> item.element );
	}

	@Override public int getModificationCount()
	{
		return hashTable.getModificationCount();
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
		assert isValidAssertion( item );
		return Optional.of( item.element );
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert canMutateAssertion();
		Item item = new Item( element );
		Optional<Item> existing = hashTable.tryAdd( item );
		if( existing.isPresent() )
			return Optional.of( existing.get().element );
		if( tail == null )
		{
			assert head == null;
			head = tail = item;
			item.prevInSet = item.nextInSet = null;
		}
		else
		{
			assert tail.nextInSet == null;
			tail.nextInSet = item;
			item.nextInSet = null;
			item.prevInSet = tail;
			tail = item;
		}
		assert isValidAssertion( item );
		return Optional.empty();
	}

	@Override public boolean tryRemove( E element )
	{
		assert canMutateAssertion();
		Item item = hashTable.tryFindByKey( element );
		if( item == null )
			return false;
		hashTable.remove( item );
		assert isValidAssertion( item );
		if( item.prevInSet == null )
			head = item.nextInSet;
		else
			item.prevInSet.nextInSet = item.nextInSet;
		if( item.nextInSet == null )
			tail = item.prevInSet;
		else
			item.nextInSet.prevInSet = item.prevInSet;
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

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@SuppressWarnings( "SameReturnValue" ) private boolean isValidAssertion( Item item )
	{
		assert item.prevInSet == null? head == item : item.prevInSet.nextInSet == item;
		assert item.nextInSet == null? tail == item : item.nextInSet.prevInSet == item;
		return true;
	}

	private final class Item extends HashNode<E,Item>
	{
		final E element;
		int hashCode = 0;
		Item prevInSet;
		Item nextInSet;

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
			if( o instanceof ConcreteMutableLinkedHashSet<?>.Item )
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
			currentNode = currentNode.nextInSet;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert currentNode != null : new IllegalStateException();
			assert isValidAssertion( currentNode );
			remove( currentNode.element );
			deleted = true;
			modificationCount++;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return currentNode == null ? "finished!" : modificationCount == hashTable.getModificationCount() ? Objects.toString( currentNode ) : "concurrent modification!";
		}
	}
}
