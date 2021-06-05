package mikenakis.kit.collections;

import mikenakis.kit.functional.Function1;

import java.util.Iterator;

public class ConvertingIterable<T,F> implements Iterable<T>
{
	private final Iterable<F> delegee;
	private final Function1<T,F> converter;

	public ConvertingIterable( Iterable<F> delegee, Function1<T,F> converter )
	{
		this.delegee = delegee;
		this.converter = converter;
	}

	@Override public Iterator<T> iterator()
	{
		Iterator<F> originalIterator = delegee.iterator();
		return new ConvertingIterator<>( originalIterator, converter );
	}
}
