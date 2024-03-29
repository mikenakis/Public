package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.ConcurrentModificationException;
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
		assert mustBeWritableAssertion();
		assert index == 0 : new IndexOutOfBoundsException();
		if( Objects.equals( theElement, element ) )
			return;
		theElement = element;
		modificationCount++;
	}

	@Override public void insertAt( int index, T element )
	{
		assert mustBeWritableAssertion();
		assert false : new UnsupportedOperationException();
	}

	@Override public void removeAt( int index )
	{
		assert mustBeWritableAssertion();
		assert false : new UnsupportedOperationException();
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		assert false : new UnsupportedOperationException();
		return true;
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		assert mustBeWritableAssertion();
		return new MyEnumerator();
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		assert mustBeReadableAssertion();
		return new MyEnumerator();
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	final class MyEnumerator implements MutableEnumerator.Defaults<T>
	{
		private final int expectedModCount;
		private int index;

		MyEnumerator()
		{
			expectedModCount = getModificationCount(); //NOTE: checking whether assertions are enabled to skip this is more expensive than just doing this.
			index = 0;
		}

		@Override public void deleteCurrent()
		{
			assert false; //this is actually a rigid collection.
		}

		@Override public boolean isFinished()
		{
			assert mustBeReadableAssertion();
			return index >= size();
		}

		@Override public T current()
		{
			assert mustBeReadableAssertion();
			assert getModificationCount() == expectedModCount : new ConcurrentModificationException();
			assert !isFinished() : new IllegalStateException();
			return get( index );
		}

		@Override public UnmodifiableEnumerator<T> moveNext()
		{
			assert mustBeReadableAssertion();
			assert getModificationCount() == expectedModCount : new ConcurrentModificationException();
			assert !isFinished() : new IllegalStateException();
			index++;
			return this;
		}

		@Override public Coherence coherence()
		{
			return SingleElementList.this.coherence();
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return unmodifiableEnumeratorToString();
		}
	}
}
