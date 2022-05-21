package io.github.mikenakis.tyraki.mutable;

import io.github.mikenakis.kit.functional.Procedure1;
import io.github.mikenakis.tyraki.MutableEnumerable;
import io.github.mikenakis.tyraki.MutableEnumerator;
import io.github.mikenakis.tyraki.UnmodifiableEnumerable;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

class MutableEnumerableOnUnmodifiableEnumerable<T> extends MutableCollectionsSubject implements MutableEnumerable.Defaults<T>, UnmodifiableEnumerable.Decorator<T>
{
	private final UnmodifiableEnumerable<T> unmodifiableEnumerable;
	private final Procedure1<T> deleter;

	MutableEnumerableOnUnmodifiableEnumerable( MutableCollections mutableCollections, UnmodifiableEnumerable<T> unmodifiableEnumerable, Procedure1<T> deleter )
	{
		super( mutableCollections );
		this.unmodifiableEnumerable = unmodifiableEnumerable;
		this.deleter = deleter;
	}

	@Override public UnmodifiableEnumerable<T> getDecoratedUnmodifiableEnumerable()
	{
		return unmodifiableEnumerable;
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		var unmodifiableEnumerator = unmodifiableEnumerable.newUnmodifiableEnumerator();
		return new MutableEnumeratorOnUnmodifiableEnumerator<>( mutableCollections, unmodifiableEnumerator, deleter );
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		return unmodifiableEnumerable.newUnmodifiableEnumerator();
	}
}
