package mikenakis.tyraki.legacy;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.coherence.exceptions.MustBeFrozenException;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * A {@link MutableCollection} operating on a {@link Collection}.
 *
 * @author michael.gr
 */
final class LegacyMutableCollectionOnJavaCollection<E> extends LegacyAbstractMutableCollection<E>
{
	private final Collection<E> collection;
	private int modificationCount = 0;

	LegacyMutableCollectionOnJavaCollection( EqualityComparator<E> equalityComparator, Collection<E> collection )
	{
		super( equalityComparator );
		assert collection != null;
		this.collection = collection;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		throw new MustBeFrozenException( null );
	}

	@Override public int hashCode()
	{
		return collection.hashCode();
	}

	@Override public int size()
	{
		return collection.size();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		if( !Kit.collection.contains( collection, element ) )
			return Optional.empty();
		return Optional.of( element ); //java collections do not support obtaining their elements.
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	@Override public boolean tryReplace( E oldElement, E newElement )
	{
		if( !Kit.collection.tryRemove( collection, oldElement ) )
			return false;
		if( !Kit.collection.tryAdd( collection, newElement ) )
		{
			boolean ok = Kit.collection.tryAdd( collection, oldElement );
			assert ok : new RuntimeException(); //operation failed: the old element was removed, the new element failed to add, and then the old element could not be restored.
			return false;
		}
		modificationCount++;
		return true;
	}

	@Override public Optional<E> tryAdd( E element )
	{
		if( !Kit.collection.tryAdd( collection, element ) )
			return Optional.of( element );
		modificationCount++;
		return Optional.empty();
	}

	@Override public boolean tryRemove( E element )
	{
		if( !Kit.collection.tryRemove( collection, element ) )
			return false;
		modificationCount++;
		return true;
	}

	@Override public boolean clear()
	{
		if( collection.isEmpty() )
			return false;
		collection.clear();
		modificationCount++;
		return true;
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		return LegacyCollections.newEnumeratorOnJavaIterator( collection.iterator(), () -> modificationCount++ );
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return newMutableEnumerator();
	}

	@Override public Iterator<E> iterator()
	{
		return collection.iterator();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " elements; modificationCount=" + modificationCount;
	}
}
