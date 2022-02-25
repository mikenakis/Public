package mikenakis.tyraki;

import mikenakis.kit.functional.Function1;

class FlatteningEnumerable<T, E> implements UnmodifiableEnumerable.Defaults<T>
{
	private final UnmodifiableEnumerable<E> primaryEnumerable;
	private final Function1<UnmodifiableEnumerable<T>,E> multiplier;

	FlatteningEnumerable( UnmodifiableEnumerable<E> primaryEnumerable, Function1<UnmodifiableEnumerable<T>,E> multiplier )
	{
		this.primaryEnumerable = primaryEnumerable;
		this.multiplier = multiplier;
	}

	@Override public boolean isFrozenAssertion()
	{
		return primaryEnumerable.isFrozenAssertion();
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		return new MyEnumerator<>( primaryEnumerable.newUnmodifiableEnumerator(), multiplier );
	}

	@Override public int getModificationCount()
	{
		return primaryEnumerable.getModificationCount();
	}

	private static class MyEnumerator<T, E> implements UnmodifiableEnumerator.Defaults<T>
	{
		private final UnmodifiableEnumerator<E> primaryEnumerator;
		private final Function1<UnmodifiableEnumerable<T>,E> multiplier;
		private UnmodifiableEnumerator<T> secondaryEnumerator;

		MyEnumerator( UnmodifiableEnumerator<E> primaryEnumerator, Function1<UnmodifiableEnumerable<T>,E> multiplier )
		{
			this.primaryEnumerator = primaryEnumerator;
			this.multiplier = multiplier;
			secondaryEnumerator = reload( primaryEnumerator, multiplier );
		}

		@Override public boolean isFinished()
		{
			return secondaryEnumerator == null;
		}

		@Override public T getCurrent()
		{
			assert secondaryEnumerator != null;
			return secondaryEnumerator.getCurrent();
		}

		@Override public UnmodifiableEnumerator<T> moveNext()
		{
			assert secondaryEnumerator != null;
			secondaryEnumerator.moveNext();
			if( secondaryEnumerator.isFinished() )
				secondaryEnumerator = reload( primaryEnumerator, multiplier );
			return this;
		}

		//TODO convert to instance method
		private static <T, E> UnmodifiableEnumerator<T> reload( UnmodifiableEnumerator<E> primaryEnumerator, Function1<UnmodifiableEnumerable<T>,E> multiplier )
		{
			for( ; ; )
			{
				if( primaryEnumerator.isFinished() )
					return null;
				E primaryElement = primaryEnumerator.getCurrent();
				primaryEnumerator.moveNext();
				UnmodifiableEnumerable<T> secondaryEnumerable = multiplier.invoke( primaryElement );
				UnmodifiableEnumerator<T> secondaryEnumerator = secondaryEnumerable.newUnmodifiableEnumerator();
				if( !secondaryEnumerator.isFinished() )
					return secondaryEnumerator;
			}
		}
	}
}
