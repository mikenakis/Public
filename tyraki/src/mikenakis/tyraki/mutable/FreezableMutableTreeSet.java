package mikenakis.tyraki.mutable;

import mikenakis.kit.EqualityComparatorOnComparator;
import mikenakis.kit.Hasher;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableHashSet;
import mikenakis.tyraki.legacy.LegacyCollections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Tree Set.
 *
 * @author michael.gr
 */
final class FreezableMutableTreeSet<T> extends AbstractMutableCollection<T> implements MutableHashSet.Defaults<T>
{
	private final Collection<Item> javaSet = new TreeSet<>();
	private int modificationCount = 0;
	final Hasher<? super T> hasher;
	final Comparator<? super T> comparator;

	FreezableMutableTreeSet( MutableCollections mutableCollections, Hasher<? super T> hasher, Comparator<? super T> comparator )
	{
		super( mutableCollections, new EqualityComparatorOnComparator<>( comparator ) );
		this.hasher = hasher;
		this.comparator = comparator;
	}

	@Override public Hasher<? super T> getElementHasher()
	{
		return hasher;
	}

	@Override public int size()
	{
		assert canReadAssertion();
		return javaSet.size();
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	@Override public Optional<T> tryGet( T element )
	{
		assert element != null;
		assert canReadAssertion();
		Item item = new Item( element );
		if( !Kit.collection.contains( javaSet, item ) )
			 return Optional.empty();
		return Optional.of( element ); //Note: the java set does not support fetching any of its contents, so the best we can do is simply return the element that was passed to us here.
	}

	@Override public Optional<T> tryAdd( T element )
	{
		assert canMutateAssertion();
		Item item = new Item( element );
		if( !Kit.collection.tryAdd( javaSet, item ) )
			return Optional.of( element );
		modificationCount++;
		return Optional.empty();
	}

	@Override public boolean tryReplace( T oldElement, T newElement )
	{
		assert canMutateAssertion();
		Item oldItem = new Item( oldElement );
		if( !Kit.collection.tryRemove( javaSet, oldItem ) )
			return false;
		Item newItem = new Item( newElement );
		Kit.collection.add( javaSet, newItem );
		modificationCount++;
		return true;
	}

	@Override public boolean tryRemove( T element )
	{
		assert canMutateAssertion();
		Item item = new Item( element );
		if( !Kit.collection.tryRemove( javaSet, item ) )
			return false;
		modificationCount++;
		return true;
	}

	@Override public boolean clear()
	{
		assert canMutateAssertion();
		if( javaSet.isEmpty() )
			return false;
		javaSet.clear();
		modificationCount++;
		return true;
	}

	private final Function1<T,Item> converter = item -> item.element;

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		Iterator<Item> iterator = javaSet.iterator();
		MutableEnumerator<Item> modifiableEnumerator = LegacyCollections.newEnumeratorOnJavaIterator( iterator, () -> modificationCount++ );
		return modifiableEnumerator.map( converter );
	}

	@Override public boolean containsDuplicates()
	{
		assert !super.containsDuplicates();
		return false;
	}

	private class Item implements Comparable<Item>
	{
		final T element;

		Item( T element )
		{
			this.element = element;
		}

		public boolean equals( Item other )
		{
			return equalityComparator.equals( element, other.element );
		}

		@Override public boolean equals( Object o )
		{
			if( o instanceof FreezableMutableTreeSet<?>.Item )
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
			return hasher.getHashCode( element );
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return String.valueOf( element );
		}

		@Override public int compareTo( Item o )
		{
			assert o != null;
			return comparator.compare( element, o.element );
		}
	}
}
