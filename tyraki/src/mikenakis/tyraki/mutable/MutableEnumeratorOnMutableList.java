package mikenakis.tyraki.mutable;

import mikenakis.tyraki.MutableList;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.ConcurrentModificationException;

final class MutableEnumeratorOnMutableList<E> extends AbstractMutableEnumerator<E>
{
	private final MutableList<E> list;
	private int expectedModCount;
	private int index;
	private boolean deleted;

	MutableEnumeratorOnMutableList( MutableCollections mutableCollections, MutableList<E> list )
	{
		super( mutableCollections );
		this.list = list;
		expectedModCount = list.getModificationCount(); //NOTE: checking whether assertions are enabled to skip this is more expensive than just doing this.
		index = 0;
		deleted = false;
	}

	@Override public void deleteCurrent()
	{
		assert list.getModificationCount() == expectedModCount : new ConcurrentModificationException();
		assert !deleted : new IllegalStateException();
		assert !isFinished() : new IllegalStateException();
		list.removeAt( index );
		deleted = true;
		expectedModCount++;
		assert list.getModificationCount() == expectedModCount; //the list should have updated its mod count.
	}

	@Override public boolean isFinished()
	{
		assert !deleted : new IllegalStateException();
		return index >= list.size();
	}

	@Override public E getCurrent()
	{
		assert list.getModificationCount() == expectedModCount : new ConcurrentModificationException();
		assert !deleted : new IllegalStateException();
		assert !isFinished() : new IllegalStateException();
		return list.get( index );
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		assert list.getModificationCount() == expectedModCount : new ConcurrentModificationException();
		if( deleted )
			deleted = false;
		else
		{
			assert !isFinished() : new IllegalStateException();
			index++;
		}
		return this;
	}
}
