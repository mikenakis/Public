package mikenakis.tyraki.conversion;

import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.tyraki.UnmodifiableList;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

class EnumeratorOnList<E> extends AbstractUnmodifiableEnumerator<E>
{
	private final UnmodifiableList<E> list;
	private final int initialModCount;
	private int index;

	EnumeratorOnList( UnmodifiableList<E> list )
	{
		assert list != null;
		this.list = list;
		initialModCount = list.getModificationCount();
		index = 0;
	}

	@Override public boolean isFinished()
	{
		return index >= list.size();
	}

	@Override public E getCurrent()
	{
		assert list.getModificationCount() == initialModCount : new ConcurrentModificationException();
		assert !isFinished() : new NoSuchElementException();
		return list.get( index );
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		assert list.getModificationCount() == initialModCount : new ConcurrentModificationException();
		assert !isFinished() : new NoSuchElementException();
		index++;
		return this;
	}
}
