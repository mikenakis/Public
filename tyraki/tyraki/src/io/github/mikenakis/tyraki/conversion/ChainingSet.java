package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * Chaining unmodifiable set.
 *
 * @author michael.gr
 */
class ChainingSet<E> extends ChainingCollection<E>
{
	/**
	 * Initializes a new instance of {@link ChainingSet}.
	 *
	 * @param collectionsToChain the {@link UnmodifiableCollection}s to chain together.
	 */
	ChainingSet( UnmodifiableCollection<UnmodifiableCollection<E>> collectionsToChain, EqualityComparator<E> equalityComparator )
	{
		super( collectionsToChain, equalityComparator );
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new MyEnumerator<>( this );
	}

	@Override public int size()
	{
		return countElements();
	}

	private static class MyEnumerator<E> extends AbstractUnmodifiableEnumerator<E>
	{
		final ChainingSet<E> set;
		final UnmodifiableEnumerator<UnmodifiableCollection<E>> collections;
		private UnmodifiableEnumerator<E> enumerator;

		MyEnumerator( ChainingSet<E> set )
		{
			this.set = set;
			collections = set.collectionsToChain.newUnmodifiableEnumerator();
			enumerator = collections.current().newUnmodifiableEnumerator();
			bump();
		}

		private boolean shouldSkip( E item )
		{
			for( UnmodifiableCollection<E> collection : set.collectionsToChain )
			{
				if( collection == collections.current() )
					break;
				if( collection.contains( item ) )
					return true;
			}
			return false;
		}

		@Override public boolean isFinished()
		{
			return enumerator == null;
		}

		@Override public E current()
		{
			assert !isFinished();
			return enumerator.current();
		}

		@Override public UnmodifiableEnumerator<E> moveNext()
		{
			enumerator.moveNext();
			bump();
			return this;
		}

		private void bump()
		{
			for( ; ; )
			{
				if( enumerator.isFinished() )
				{
					collections.moveNext();
					if( collections.isFinished() )
					{
						enumerator = null;
						break;
					}
					enumerator = collections.current().newUnmodifiableEnumerator();
					continue;
				}
				if( shouldSkip( enumerator.current() ) )
				{
					enumerator.moveNext();
					continue;
				}
				break;
			}
		}
	}
}
