package mikenakis.tyraki.legacy;

import mikenakis.kit.DefaultEqualityComparator;
import mikenakis.kit.mutation.NotFrozenException;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.MutableList;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * A {@link MutableList} on a {@link List}.
 *
 * @author michael.gr
 */
//IntellijIdea blooper: 'mapToValues(Function1<? extends V, ? super E>)' in 'saganaki.collections.UnmodifiableCollection.Defaults' clashes with 'mapToValues(Function1<? extends
// V, ? super E>)' in 'saganaki.collections.UnmodifiableList'; attempting to use incompatible return type
final class LegacyMutableListOnJavaList<E> extends LegacyAbstractMutableCollection<E> implements MutableList.Defaults<E>
{
	private final List<E> javaList;
	private int modificationCount = 0;

	LegacyMutableListOnJavaList( List<E> javaList )
	{
		super( DefaultEqualityComparator.getInstance() );
		assert javaList != null;
		this.javaList = javaList;
	}

	@Override public boolean isFrozenAssertion()
	{
		throw new NotFrozenException( null );
	}

	@Override public boolean canMutateAssertion()
	{
		return true; //we have no way of determining this with a java collection.
	}

	@Override public int hashCode()
	{
		return javaList.hashCode();
	}

	@Override public MutableEnumerator<E> newMutableEnumerator()
	{
		return LegacyCollections.newEnumeratorOnJavaIterator( javaList.iterator(), () -> modificationCount++ );
	}

	@Override public Iterator<E> iterator()
	{
		return javaList.iterator();
	}

	@Override public int size()
	{
		return javaList.size();
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	@Override public E get( int index )
	{
		return javaList.get( index );
	}

	@Override public int indexOf( E element )
	{
		return javaList.indexOf( element );
	}

	@Override public boolean clear()
	{
		if( javaList.isEmpty() )
			return false;
		javaList.clear();
		modificationCount++;
		return true;
	}

	@Override public void replaceAt( int index, E element )
	{
		javaList.set( index, element );
		modificationCount++;
	}

	@Override public void insertAt( int index, E element )
	{
		javaList.add( index, element );
		modificationCount++;
	}

	@Override public void removeAt( int index )
	{
		javaList.remove( index );
		modificationCount++;
	}

	@Override public Optional<E> tryAdd( E element )
	{
		insertAt( size(), element );
		return Optional.empty();
	}
}
