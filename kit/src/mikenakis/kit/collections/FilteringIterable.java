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
}
