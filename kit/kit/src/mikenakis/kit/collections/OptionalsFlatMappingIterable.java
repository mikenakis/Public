package mikenakis.kit.collections;

import mikenakis.kit.functional.Function1;

import java.util.Iterator;
import java.util.Optional;

public class OptionalsFlatMappingIterable<T,F> implements Iterable<T>
{
	private final Iterable<F> delegee;
	private final Function1<Optional<T>,F> converterAndFilterer;

	public OptionalsFlatMappingIterable( Iterable<F> delegee, Function1<Optional<T>,F> converterAndFilterer )
	{
		this.delegee = delegee;
		this.converterAndFilterer = converterAndFilterer;
	}

	@Override public Iterator<T> iterator()
	{
		Iterator<F> unfilteredIterator = delegee.iterator();
		return new ConvertingAndFilteringIterator<>( unfilteredIterator, converterAndFilterer );
	}

	private static class ConvertingAndFilteringIterator<T,F> implements Iterator<T>
	{
		private final Iterator<F> delegee;
		private final Function1<Optional<T>,F> converterAndFilterer;
		private Optional<T> current = Optional.empty();

		public ConvertingAndFilteringIterator( Iterator<F> delegee, Function1<Optional<T>,F> converterAndFilterer )
		{
			this.delegee = delegee;
			this.converterAndFilterer = converterAndFilterer;
			skipToMatch();
		}

		@Override public boolean hasNext()
		{
			return current.isPresent();
		}

		@Override public T next()
		{
			T result = current.orElseThrow();
			skipToMatch();
			return result;
		}

		private void skipToMatch()
		{
			for(; ; )
			{
				if( !delegee.hasNext() )
				{
					current = Optional.empty();
					break;
				}
				F next = delegee.next();
				current = converterAndFilterer.invoke( next );
				if( current.isPresent() )
					break;
			}
		}
	}
}
