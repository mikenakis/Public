package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.MutableHashSet;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * A Set implemented using a {@link java.util.HashSet}.
 *
 * @author michael.gr
 */
final class ConcreteMutableHashSet<E> extends AbstractMutableCollection<E> implements MutableHashSet.Defaults<E>
{
	private final HashTable<E,Node> hashTable;

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
		Node node = hashTable.tryFindByKey( element );
		if( node == null )
			return Optional.empty();
		return Optional.of( node.element );
	}

	@Override public Optional<E> tryAdd( E element )
	{
		assert mustBeWritableAssertion();
		Node node = new Node( element );
		return hashTable.tryAdd( node ).map( existing -> existing.element );
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		assert mustBeWritableAssertion();
		Node oldNode = hashTable.tryFindByKey( oldElement );
		if( oldNode == null )
			return false;
		if( hashTable.tryFindByKey( newElement ) != null )
			return false;
		hashTable.remove( oldNode );
		Node newNode = new Node( newElement );
		Optional<Node> existing = hashTable.tryAdd( newNode );
		assert existing.isEmpty();
		return true;
	}

	@Override public boolean tryRemove( E element )
	{
		assert mustBeWritableAssertion();
		Node oldNode = hashTable.tryFindByKey( element );
		if( oldNode == null )
			return false;
		hashTable.remove( oldNode );
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

	private final class Node extends HashNode<E,Node>
	{
		final E element;
		int hashCode = 0;

		Node( E element )
		{
			assert element != null;
			this.element = element;
		}

		public boolean equals( Node other )
		{
			return equalityComparator.equals( element, other.element );
		}

		@Override public boolean equals( Object o )
		{
			if( o instanceof ConcreteMutableHashSet<?>.Node )
			{
				@SuppressWarnings( "unchecked" ) Node otherNode = (Node)o;
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
}
