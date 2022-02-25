package mikenakis.tyraki.mutable;

import mikenakis.kit.functional.Procedure1;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerable;
import mikenakis.tyraki.UnmodifiableEnumerator;

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

	@Override public boolean canMutateAssertion()
	{
		assert super.canMutateAssertion();
		return true;
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		return unmodifiableEnumerable.newUnmodifiableEnumerator();
	}
}
