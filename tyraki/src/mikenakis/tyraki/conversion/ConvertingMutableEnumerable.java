package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.MutableEnumerable;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

final class ConvertingMutableEnumerable<T,F> extends AbstractUnmodifiableEnumerable<T> implements MutableEnumerable.Defaults<T>
{
	private final MutableEnumerable<F> enumerableToConvert;
	private final Function1<? extends T,? super F> converter;

	ConvertingMutableEnumerable( MutableEnumerable<F> enumerableToConvert, Function1<? extends T,? super F> converter )
	{
		this.enumerableToConvert = enumerableToConvert;
		this.converter = converter;
	}

	@Override public UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> unmodifiableEnumerator = enumerableToConvert.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( unmodifiableEnumerator, ( i, e ) -> converter.invoke( e ) );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		MutableEnumerator<F> mutableEnumerator = enumerableToConvert.newMutableEnumerator();
		return new ConvertingMutableEnumerator<>( mutableEnumerator, e -> converter.invoke( e ) );
	}

	@Override public int getModificationCount()
	{
		return enumerableToConvert.getModificationCount();
	}

	@Override public boolean isFrozen()
	{
		return enumerableToConvert.isFrozen();
	}

	@Override public boolean canWriteAssertion()
	{
		return enumerableToConvert.canWriteAssertion();
	}
}
