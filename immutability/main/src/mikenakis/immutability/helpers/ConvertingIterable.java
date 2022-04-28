package mikenakis.immutability.helpers;

import mikenakis.immutability.mykit.functional.Function1;

import java.util.Iterator;

public class ConvertingIterable<T, F> implements Iterable<T>
{
	public final Iterable<F> sourceIterable;
	public final Function1<T,F> converter;

	public ConvertingIterable( Iterable<F> sourceIterable, Function1<T,F> converter )
	{
		this.sourceIterable = sourceIterable;
		this.converter = converter;
	}

	@Override public Iterator<T> iterator()
	{
		Iterator<F> sourceIterator = sourceIterable.iterator();
		return new Iterator<>()
		{
			@Override public boolean hasNext()
			{
				return sourceIterator.hasNext();
			}
			@Override public T next()
			{
				F sourceElement = sourceIterator.next();
				return converter.invoke( sourceElement );
			}
		};
	}
}
