package io.github.mikenakis.tyraki.legacy;

import io.github.mikenakis.coherence.Coherence;
import io.github.mikenakis.coherence.NullaryCoherence;
import io.github.mikenakis.kit.functional.Procedure0;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

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

	@Override public E current()
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

	@Override public Coherence coherence()
	{
		return NullaryCoherence.instance;
	}
}
