package mikenakis.testana.kit;

import java.util.Iterator;

public class UnmodifiableIterator<E> implements Iterator<E>
{
	private final Iterator<? extends E> delegee;

	public UnmodifiableIterator( Iterator<? extends E> delegee )
	{

		this.delegee = delegee;
	}

	@Override public boolean hasNext()
	{
		return delegee.hasNext();
	}

	@Override public E next()
	{
		return delegee.next();
	}

	@Deprecated @Override public final void remove()
	{
		throw new UnsupportedOperationException();
	}
}
