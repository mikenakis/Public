package mikenakis.tyraki.legacy;

import mikenakis.kit.functional.Procedure0;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Iterator;

/**
 * {@link MutableEnumerator} on java {@link Iterator}.
 *
 * @author michael.gr
 */
final class LegacyMutableEnumeratorOnJavaIterator<E> implements MutableEnumerator.Defaults<E>
{
	final Iterator<E> iterator;
	private E current;
	private boolean hasCurrent;
	private final Procedure0 modCountIncrementer;
	private boolean deleted;

	LegacyMutableEnumeratorOnJavaIterator( Iterator<E> iterator, Procedure0 modCountIncrementer )
	{
		assert iterator != null;
		assert modCountIncrementer != null;
		this.iterator = iterator;
		reload();
		this.modCountIncrementer = modCountIncrementer;
		deleted = false;
	}

	private void reload()
	{
		hasCurrent = iterator.hasNext();
		current = hasCurrent ? iterator.next() : null;
	}

	@Override public boolean isFinished()
	{
		assert !deleted : new IllegalStateException();
		return !hasCurrent;
	}

	@Override public E getCurrent()
	{
		assert !isFinished() : new IllegalStateException();
		return current;
	}

	@Override public UnmodifiableEnumerator<E> moveNext()
	{
		if( deleted )
			deleted = false;
		assert hasCurrent : new IllegalStateException(); //moveNext() while finished.
		reload();
		return this;
	}

	@Override public void deleteCurrent()
	{
		assert !isFinished() : new IllegalStateException();
		assert !deleted : new IllegalStateException();
		iterator.remove();
		deleted = true;
		modCountIncrementer.invoke();
	}
}
