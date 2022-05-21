package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.functional.Procedure1;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

class MutableEnumeratorOnUnmodifiableEnumerator<T> extends MutableCollectionsSubject implements MutableEnumerator.Defaults<T>, UnmodifiableEnumerator.Decorator<T>
{
	private final UnmodifiableEnumerator<T> unmodifiableEnumerator;
	private final Procedure1<T> deleter;

	MutableEnumeratorOnUnmodifiableEnumerator( MutableCollections mutableCollections, UnmodifiableEnumerator<T> unmodifiableEnumerator, Procedure1<T> deleter )
	{
		super( mutableCollections );
		this.unmodifiableEnumerator = unmodifiableEnumerator;
		this.deleter = deleter;
	}

	@Override public UnmodifiableEnumerator<T> getDecoratedUnmodifiableEnumerator()
	{
		return unmodifiableEnumerator;
	}

	@Override public void deleteCurrent()
	{
		T element = current();
		deleter.invoke( element );
	}
}
