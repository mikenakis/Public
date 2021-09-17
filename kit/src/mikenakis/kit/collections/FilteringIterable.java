package mikenakis.kit.collections;

import mikenakis.kit.functional.BooleanFunction1;

import java.util.Iterator;

public class FilteringIterable<E> implements Iterable<E>
{
	private final Iterable<? extends E> delegee;
	private final BooleanFunction1<E> filter;

	public FilteringIterable( Iterable<? extends E> delegee, BooleanFunction1<E> filter )
	{
		this.delegee = delegee;
		this.filter = filter;
	}

	@Override public Iterator<E> iterator()
	{
		Iterator<? extends E> unfilteredIterator = delegee.iterator();
		return new FilteringIterator<>( unfilteredIterator, filter );
	}

	private static class FilteringIterator<E> extends UnmodifiableIterator<E>
	{
		private final BooleanFunction1<E> filter;
		private E current = null;

		public FilteringIterator( Iterator<? extends E> delegee, BooleanFunction1<E> filter )
		{
			super( delegee );
			this.filter = filter;
			skipToMatch();
		}

		@Override public boolean hasNext()
		{
			return current != null;
		}

		@Override public E next()
		{
			E result = current;
			skipToMatch();
			return result;
		}

		private void skipToMatch()
		{
			for(; ; )
			{
				if( !delegee.hasNext() )
				{
					current = null;
					break;
				}
				current = delegee.next();
				if( filter.invoke( current ) )
					break;
			}
		}
	}
}
