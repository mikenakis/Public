package io.github.mikenakis.kit.collections;

import io.github.mikenakis.kit.functional.Function1;

import java.util.Iterator;

public class MappingIterable<T,F> implements Iterable<T>
{
	private final Iterable<F> delegee;
	private final Function1<T,F> converter;

	public MappingIterable( Iterable<F> delegee, Function1<T,F> converter )
	{
		this.delegee = delegee;
		this.converter = converter;
	}

	@Override public Iterator<T> iterator()
	{
		Iterator<F> originalIterator = delegee.iterator();
		return new ConvertingIterator<>( originalIterator, converter );
	}

	private static class ConvertingIterator<F, T> implements Iterator<T>
	{
		private final Iterator<? extends F> delegee;
		private final Function1<T,F> converter;

		ConvertingIterator( Iterator<? extends F> delegee, Function1<T,F> converter )
		{
			this.delegee = delegee;
			this.converter = converter;
		}

		@Override public final boolean hasNext()
		{
			return delegee.hasNext();
		}

		@Override public final T next()
		{
			F from = delegee.next();
			return converter.invoke( from );
		}

		@Override public final void remove()
		{
			delegee.remove();
		}
	}
}
