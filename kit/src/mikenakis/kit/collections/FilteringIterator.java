package mikenakis.kit.collections;

import mikenakis.kit.functional.BooleanFunction1;

import java.util.Iterator;

public class FilteringIterator<E> extends UnmodifiableIterator<E>
{
	private final BooleanFunction1<E> filter;
	private E current = null;

	public FilteringIterator( Iterator<? extends E> delegee, BooleanFunction1<E> filter )
	{
		super( delegee );
		this.filter = filter;
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
