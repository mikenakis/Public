package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function1;
import mikenakis.kit.mutation.MutationContext;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

final class ConvertingMutableEnumerable<T,F> extends AbstractUnmodifiableEnumerable<T> implements MutableEnumerable.Defaults<T>
{
	private final MutableEnumerable<F> enumerable;
	private final Function1<? extends T,? super F> converter;

	ConvertingMutableEnumerable( MutableEnumerable<F> enumerable, Function1<? extends T,? super F> converter )
	{
		this.enumerable = enumerable;
		this.converter = converter;
	}

	@Override public MutationContext mutationContext()
	{
		return enumerable.mutationContext();
	}

	@Override public boolean isImmutableAssertion()
	{
		return enumerable.isImmutableAssertion();
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> unmodifiableEnumerator = enumerable.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( unmodifiableEnumerator, ( i, e ) -> converter.invoke( e ) );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		MutableEnumerator<F> mutableEnumerator = enumerable.newMutableEnumerator();
		return new ConvertingMutableEnumerator<>( mutableEnumerator, converter );
	}

	@Override public int getModificationCount()
	{
		return enumerable.getModificationCount();
	}
}
