package mikenakis.kit;

import java.util.Iterator;

/**
 * A decorator of {@link Iterator} which prevents mutations.
 * Slightly mitigates Java's deplorable dumbfuckery of baking into the language a {@link Iterator} interface which is mutable.
 * The resulting {@link Iterator} still looks mutable of course, but at least it asserts that no mutation is ever attempted.
 */
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
