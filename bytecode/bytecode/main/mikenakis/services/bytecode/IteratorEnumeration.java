package mikenakis.services.bytecode;

import java.util.Enumeration;
import java.util.Iterator;

//TODO DELETE
public class IteratorEnumeration<E> implements Enumeration<E>
{
	private final Iterator<? extends E> iterator;

	public IteratorEnumeration( Iterator<? extends E> iterator )
	{
		this.iterator = iterator;
	}

	@Override public boolean hasMoreElements()
	{
		return iterator.hasNext();
	}

	@Override public E nextElement()
	{
		return iterator.next();
	}
}
