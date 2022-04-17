package mikenakis.tyraki.mutable;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.Queue;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.Optional;

/**
 * A Linked {@link Queue}.
 *
 * @author michael.gr
 */
final class LinkedQueue<E> extends MutableCollectionsSubject implements Queue.Defaults<E>
{
	private Item head;
	private Item tail;
	private int size;
	private int modificationCount;

	LinkedQueue( MutableCollections mutableCollections )
	{
		super( mutableCollections );
	}

	@Override public int getModificationCount()
	{
		return modificationCount;
	}

	@Override public int size()
	{
		return size;
	}

	@Override public boolean enqueue( E element )
	{
		assert mustBeWritableAssertion();
		Item item = new Item( element );
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
		modificationCount++;
		size++;
		assert mustBeValidAssertion( item );
		return true;
	}

	@Override public Optional<E> tryDequeue()
	{
		assert mustBeWritableAssertion();
		if( size == 0 )
			return Optional.empty();
		Item item = head;
		assert item != null;
		remove( item );
		return Optional.of( item.element );
	}

	@Override public boolean clear()
	{
		assert mustBeWritableAssertion();
		if( size == 0 )
		{
			assert head == null;
			assert tail == null;
			return false;
		}
		head = null;
		tail = null;
		modificationCount++;
		size = 0;
		return true;
	}

	private void remove( Item item )
	{
		assert mustBeValidAssertion( item );
		if( item.prevInSet == null )
			head = item.nextInSet;
		else
			item.prevInSet.nextInSet = item.nextInSet;
		if( item.nextInSet == null )
			tail = item.prevInSet;
		else
			item.nextInSet.prevInSet = item.prevInSet;
		modificationCount++;
		size--;
	}

	@SuppressWarnings( "SameReturnValue" ) private boolean mustBeValidAssertion( Item item )
	{
		assert item.prevInSet == null? head == item : item.prevInSet.nextInSet == item;
		assert item.nextInSet == null? tail == item : item.nextInSet.prevInSet == item;
		return true;
	}

	private final class Item
	{
		final E element;
		Item prevInSet;
		Item nextInSet;

		Item( E element )
		{
			this.element = element;
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
		int modificationCount = LinkedQueue.this.modificationCount;

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

		@Override public Item current()
		{
			assert modificationCount == LinkedQueue.this.modificationCount : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			assert !deleted : new IllegalStateException();
			assert mustBeValidAssertion( currentNode );
			return currentNode;
		}

		@Override public UnmodifiableEnumerator<Item> moveNext()
		{
			assert modificationCount == LinkedQueue.this.modificationCount : new ConcurrentModificationException();
			assert currentNode != null : new IllegalStateException();
			deleted = false;
			currentNode = currentNode.nextInSet;
			return this;
		}

		@Override public void deleteCurrent()
		{
			assert modificationCount == LinkedQueue.this.modificationCount : new ConcurrentModificationException();
			assert !deleted : new IllegalStateException();
			assert currentNode != null : new IllegalStateException();
			assert mustBeValidAssertion( currentNode );
			remove( currentNode );
			deleted = true;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return currentNode == null ? "finished!" : modificationCount == LinkedQueue.this.modificationCount ? Objects.toString( currentNode ) : "concurrent modification!";
		}
	}
}
