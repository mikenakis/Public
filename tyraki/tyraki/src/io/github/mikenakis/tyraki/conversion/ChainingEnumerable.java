package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.tyraki.DebugView;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

/**
 * Chaining {@link UnmodifiableEnumerable}.
 *
 * @author michael.gr
 */
final class ChainingEnumerable<E> extends AbstractUnmodifiableEnumerable<E>
{
	@SuppressWarnings( { "unused", "FieldNamingConvention" } )
	private final DebugView _debugView = DebugView.create( () -> this );

	private final UnmodifiableCollection<UnmodifiableEnumerable<E>> enumerablesToChain;

	/**
	 * Initializes a new instance of {@link ChainingEnumerable}.
	 *
	 * @param enumerablesToChain the {@link UnmodifiableEnumerable}s to chain together.
	 */
	ChainingEnumerable( UnmodifiableCollection<UnmodifiableEnumerable<E>> enumerablesToChain )
	{
		this.enumerablesToChain = enumerablesToChain;
	}

	@Override public UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
	{
		return new MyEnumerator<>( this );
	}

	@Override public int getModificationCount()
	{
		int sum = 0;
		for( UnmodifiableEnumerable<E> enumerable : enumerablesToChain )
			sum += enumerable.getModificationCount();
		return sum;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return enumerablesToChain.mustBeImmutableAssertion() && enumerablesToChain.trueForAll( e -> e.mustBeImmutableAssertion() );
	}

	private static final class MyEnumerator<E> extends AbstractUnmodifiableEnumerator<E>
	{
		private final UnmodifiableEnumerator<UnmodifiableEnumerable<E>> enumerablesToChain;
		private UnmodifiableEnumerator<E> enumerator = null;

		MyEnumerator( ChainingEnumerable<E> chainingEnumerable )
		{
			enumerablesToChain = chainingEnumerable.enumerablesToChain.newUnmodifiableEnumerator();
			loadNextEnumerator();
		}

		private void loadNextEnumerator()
		{
			for( ; ; )
			{
				if( enumerablesToChain.isFinished() )
				{
					enumerator = null;
					break;
				}
				enumerator = enumerablesToChain.fetchCurrentAndMoveNext().newUnmodifiableEnumerator();
				if( !enumerator.isFinished() )
					break;
			}
		}

		@Override public boolean isFinished()
		{
			return enumerator == null;
		}

		@Override public E current()
		{
			assert !isFinished() : new IllegalStateException();
			return enumerator.current();
		}

		@Override public UnmodifiableEnumerator<E> moveNext()
		{
			assert !isFinished() : new IllegalStateException();
			enumerator.moveNext();
			if( enumerator.isFinished() )
				loadNextEnumerator();
			return this;
		}
	}
}
