package mikenakis.tyraki.mutable;

import mikenakis.kit.functional.Procedure1;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

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
