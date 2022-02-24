package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableEnumerator;
import mikenakis.kit.EqualityComparator;

import java.util.Objects;

/**
 * Single Element List.
 *
 * @author michael.gr
 */
final class SingleElementList<T> extends AbstractMutableList<T>
{
	private T theElement;
	private int modificationCount = 0;

	SingleElementList( MutableCollections mutableCollections, EqualityComparator<? super T> equalityComparator, T theElement )
	{
		super( mutableCollections, equalityComparator );
		this.theElement = theElement;
	}

	@Override public int size()
	{
		return 1;
	}

	@Override public T get( int index )
	{
		assert index == 0;
		return theElement;
	}

	@Override public void replaceAt( int index, T element )
	{
		assert canWriteAssertion();
		assert index == 0 : new IndexOutOfBoundsException();
		if( Objects.equals( theElement, element ) )
			return;
		theElement = element;
		modificationCount++;
	}

	@Override public void insertAt( int index, T element )
	{
		assert canWriteAssertion();
		assert false : new UnsupportedOperationException();
	}

	@Override public void removeAt( int index )
	{
		assert canWriteAssertion();
		assert false : new UnsupportedOperationException();
	}

	@Override public boolean clear()
	{
		assert canWriteAssertion();
		assert false : new UnsupportedOperationException();
		return true;
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		return new MutableEnumeratorOnMutableList<>( mutableCollections, this );
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}
}
