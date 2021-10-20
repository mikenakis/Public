package mikenakis.kit.collections;

import java.util.Iterator;

/**
 * A decorator of {@link Iterable} which prevents mutations.
 * Slightly mitigates Java's deplorable dumbfuckery of baking into the language an {@link Iterable} interface which is mutable.
 * The resulting {@link Iterable} still looks mutable of course, but at least it asserts that no mutation is ever attempted.
 */
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
