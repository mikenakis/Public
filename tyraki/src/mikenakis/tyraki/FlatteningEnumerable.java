package mikenakis.tyraki;

import mikenakis.kit.functional.Function1;

class FlatteningEnumerable<T, E> implements UnmodifiableEnumerable.Defaults<T>
{
	private final UnmodifiableEnumerable<E> primaryEnumerable;
	private final Function1<UnmodifiableEnumerable<T>,E> converter;

	FlatteningEnumerable( UnmodifiableEnumerable<E> primaryEnumerable, Function1<UnmodifiableEnumerable<T>,E> converter )
	{
		this.primaryEnumerable = primaryEnumerable;
		this.converter = converter;
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		return new MyEnumerator<>( primaryEnumerable.newUnmodifiableEnumerator(), converter );
	}

	@Override public int getModificationCount()
	{
		return primaryEnumerable.getModificationCount();
	}

	@Override public boolean isFrozen()
	{
		return primaryEnumerable.isFrozen();
	}

	private static class MyEnumerator<T, E> implements UnmodifiableEnumerator.Defaults<T>
	{
		private final Function1<UnmodifiableEnumerable<T>,E> converter;
		private final UnmodifiableEnumerator<E> primaryEnumerator;
		private UnmodifiableEnumerator<T> secondaryEnumerator;

		MyEnumerator( UnmodifiableEnumerator<E> primaryEnumerator, Function1<UnmodifiableEnumerable<T>,E> converter )
		{
			this.converter = converter;
			this.primaryEnumerator = primaryEnumerator;
			secondaryEnumerator = reload( primaryEnumerator, converter );
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
				secondaryEnumerator = reload( primaryEnumerator, converter );
			return this;
		}

		private static <T, E> UnmodifiableEnumerator<T> reload( UnmodifiableEnumerator<E> primaryEnumerator, Function1<UnmodifiableEnumerable<T>,E> converter )
		{
			for( ; ; )
			{
				if( primaryEnumerator.isFinished() )
					return null;
				E primaryElement = primaryEnumerator.getCurrent();
				primaryEnumerator.moveNext();
				UnmodifiableEnumerable<T> secondaryEnumerable = converter.invoke( primaryElement );
				UnmodifiableEnumerator<T> secondaryEnumerator = secondaryEnumerable.newUnmodifiableEnumerator();
				if( !secondaryEnumerator.isFinished() )
					return secondaryEnumerator;
			}
		}
	}
}
