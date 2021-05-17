package mikenakis.testana.kit;

import java.util.Iterator;

public class UnmodifiableIterable<E> implements Iterable<E>
{
	private final Iterable<? extends E> delegee;

	public UnmodifiableIterable( Iterable<? extends E> delegee )
	{

		this.delegee = delegee;
	}

	@Override public Iterator<E> iterator()
	{
		Iterator<? extends E> modifiableIterator = delegee.iterator();
		return new UnmodifiableIterator<>( modifiableIterator );
	}
}
