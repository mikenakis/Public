package mikenakis.kit.collections;

import mikenakis.kit.functional.Function1;

import java.util.Iterator;

public class ConvertingIterator<F, T> implements Iterator<T>
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
