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
	private final HashTable<E,Node> hashTable;
	private Node head;
	private Node tail;

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
		assert mustBeReadableAssertion();
		return new MyEnumerator().map( item -> item.element );
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return newMutableEnumerator();
	}

	@Override public int getModificationCount()
	{
		return hashTable.getModificationCount();
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
		Node node = hashTable.tryFindByKey( element );
		if( node == null )
			return Optional.empty();
		assert mustBeValidAssertion( node );
		return Optional.of( node.element );
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert mustBeWritableAssertion();
		Node node = new Node( element );
		Optional<Node> existing = hashTable.tryAdd( node );
		if( existing.isPresent() )
			return Optional.of( existing.get().element );
		if( tail == null )
		{
			assert head == null;
			head = tail = node;
			node.prevInSet = node.nextInSet = null;
		}
		else
		{
			assert tail.nextInSet == null;
			tail.nextInSet = node;
			node.nextInSet = null;
			node.prevInSet = tail;
			tail = node;
		}
		assert mustBeValidAssertion( node );
		return Optional.empty();
	}

	@Override public boolean tryRemove( E element )
	{
		assert mustBeWritableAssertion();
		Node node = hashTable.tryFindByKey( element );
		if( node == null )
			return false;
		hashTable.remove( node );
		assert mustBeValidAssertion( node );
		if( node.prevInSet == null )
			head = node.nextInSet;
		else
			node.prevInSet.nextInSet = node.nextInSet;
		if( node.nextInSet == null )
			tail = node.prevInSet;
		else
			node.nextInSet.prevInSet = node.prevInSet;
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

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	@SuppressWarnings( "SameReturnValue" ) private boolean mustBeValidAssertion( Node node )
	{
		assert node.prevInSet == null? head == node : node.prevInSet.nextInSet == node;
		assert node.nextInSet == null? tail == node : node.nextInSet.prevInSet == node;
		return true;
	}

	private final class Node extends HashNode<E,Node>
	{
		final E element;
		int hashCode = 0;
		Node prevInSet;
		Node nextInSet;

		Node( E element )
		{
			assert element != null;
			this.element = element;
		}

		public boolean equals( Node otherNode )
		{
			return equalityComparator.equals( element, otherNode.element );
		}

		@Override public boolean equals( Object other )
		{
			if( other instanceof ConcreteMutableLinkedHashSet<?>.Node )
			{
				@SuppressWarnings( "unchecked" ) Node otherNode = (Node)other;
				return equals( otherNode );
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
			currentNode = currentNode.nextInSet;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert modificationCount == hashTable.getModificationCount() : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert currentNode != null : new IllegalStateException();
			assert mustBeValidAssertion( currentNode );
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
