package mikenakis.bytecode.kit;

import java.util.Iterator;

/**
 * An observable {@link Iterator}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public class ObservableIterator<E> implements Iterator<E>
{
	private final Iterator<E> delegee;
	private final Runnable observer;

	public ObservableIterator( Iterator<E> delegee, Runnable observer )
	{
		this.delegee = delegee;
		this.observer = observer;
	}

	@Override public boolean hasNext()
	{
		return delegee.hasNext();
	}

	@Override public E next()
	{
		return delegee.next();
	}

	@Override public void remove()
	{
		delegee.remove();
		observer.run();
	}
}
